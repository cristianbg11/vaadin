import dominio.Evento
import dominio.Usuario

class BootStrap {

    def init = { servletContext ->
        def gerente = new Usuario(username: 'gerente', enabled: true, password: 'gerente')
        gerente.email = "cristian_bueno@hotmail.com"
        gerente.nombre = "cristian"
        gerente.apellido = "Bueno"
        gerente.save(flush: true)
        /*def evento = new Evento(caption: 'Evento',start: new Date(),  end: new Date(), minutosAntes: 5,haSidoEnviado: true, styleName: "blue", description: "lol")
        evento.setUsuario(gerente)
        evento.save(flush: true)

        if(evento.hasErrors())
        {
            println(evento.errors)
        }*/
    }
    def destroy = {
    }
}
