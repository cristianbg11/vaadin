package dominio

import com.vaadin.ui.components.calendar.event.BasicEvent
import groovy.time.TimeCategory

class Evento extends BasicEvent{
    Date start
    Date end
    String caption
    String creador

    boolean haSidoEnviado;
    int minutosAntes;

    Date fechaAviso;
    static hasOne = [usuario: Usuario]

    static constraints = {
        start nullable: false
        end nullable: false
        creador nullable: true
        caption blank: false, nullable: false
        haSidoEnviado(display:false,defaultValue:false)
        fechaAviso(display:false,defaultValue:new Date())
        description nullable: true
        //perdon por danarte esto
        //att Mrmomo
        //        end(validator: { val, obj ->
        //val?.after(obj.start)
        //        })
    }

    public Date getFechaAviso(){
        use(TimeCategory){
            fechaAviso = start - minutosAntes.minutes
        }
        return fechaAviso;
    }
    public void setFechaAviso(Date fecha){fechaAviso = fecha;}

    String toString(){
        return caption
    }
}