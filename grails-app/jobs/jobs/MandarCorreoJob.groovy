package jobs

import dominio.Evento
import groovy.time.TimeCategory

class MandarCorreoJob {
    def mailService;
    def mandrillService;
    static triggers = {
        simple repeatInterval: 5000l // execute job once in 5 seconds
    }

    def execute() {

        Date ahora= new Date();
        Date tiempoFuturo = new Date();
        use(TimeCategory){
            tiempoFuturo = ahora + 5.minute;
            ahora = ahora - 10.minutes;
        }

        def todoLosEventos = Evento.getAll();
        def eventos = Evento.findAllByFechaAvisoBetweenAndHaSidoEnviado(ahora,tiempoFuturo,false)

        //println "eventos disponibles ${eventos.size()} todos los eventos ${todoLosEventos.size()}"
        eventos.collect{ evento     ->
                mailService.sendMail {
                    to evento.usuario.email
                    subject "Recordatorio del evento ${evento.caption}"
                    body ("${evento.usuario.nombre} queremos informarle que el evento ${evento.caption} esta cerca")
                }
                println "mail sent to ${evento.usuario.nombre}"

            evento.haSidoEnviado = true;
            evento.save flush: true
        }
    }
}
