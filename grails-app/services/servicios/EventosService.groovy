package servicios

import com.vaadin.ui.components.calendar.event.BasicEvent
import dominio.Evento
import dominio.Usuario
import grails.transaction.Transactional

@Transactional
class EventosService {

    def crearEvento(BasicEvent event) {
        def evento = new Evento(caption: event.caption, start: event.start, end: event.end, styleName: event.styleName, description: event.description)
        evento.save(flush: true)
    }
    def restaurarEventos(List<Evento> events)
    {
        Evento.executeUpdate('delete from Evento')
        events.each {event ->
            def evento = new Evento(caption: event.caption, start: event.start, end: event.end, styleName: event.styleName, description: event.description, usuario: Usuario.first(), minutosAntes: event.minutosAntes, haSidoEnviado: event.haSidoEnviado)
            evento.save(flush: true)
            if(evento.hasErrors())
            {
                println(evento.errors)
            }
        }
    }

}
