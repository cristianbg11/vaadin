package app

import com.vaadin.annotations.Theme
import com.vaadin.server.VaadinSession
import com.vaadin.ui.Alignment
import com.vaadin.ui.Calendar
import com.vaadin.ui.MenuBar
import com.vaadin.ui.UI
import com.vaadin.ui.VerticalLayout
import com.vaadin.server.VaadinRequest
import com.vaadin.ui.Label
import com.vaadin.grails.Grails
import dominio.Usuario

/**
 *
 *
 * @author
 */
@Theme("valo")
class MyUI extends UI {

    public final static String USUARIO_LOGUEADO="USUARIO_LOGUEADO";
    VerticalLayout layout = new VerticalLayout()
    public static final String NAME = "Principal";
    @Override
    protected void init(VaadinRequest vaadinRequest) {

        Calendario calendar = new Calendario();
        def usuario=(Usuario)getSession().session.getAttribute(MyUI.USUARIO_LOGUEADO);
        layout.setMargin(true)
        VerticalLayout capaActual = new VerticalLayout()
        VerticalLayout capaActual2 = new VerticalLayout()
        MenuBar menubar = new MenuBar()
        layout.addComponent(menubar)
        layout.setComponentAlignment(menubar, Alignment.TOP_CENTER)
        //menubar.setSizeFull()
        layout.addComponent(capaActual)

        if(usuario == null)
        {
            def loginForm = new LoginForm()
            layout.replaceComponent(capaActual, loginForm)

            capaActual = loginForm
        }
        if(usuario!=null)
        {
            layout.replaceComponent(capaActual, calendar)
            MenuBar.MenuItem menuEdit = menubar.addItem("Editar Usuario", null, new MenuBar.Command() {
                @Override
                void menuSelected(MenuBar.MenuItem menuItem) {

                    def usuarioEdit = new UsuarioEdit(usuario)
                    layout.replaceComponent(calendar, usuarioEdit)
                    //layout.replaceComponent(capaActual2, usuarioEdit)

                    capaActual = usuarioEdit

                }
            });
            MenuBar.MenuItem menuCalendario = menubar.addItem("Ver calendario", null, new MenuBar.Command() {
                @Override
                void menuSelected(MenuBar.MenuItem menuItem) {


                    layout.replaceComponent(capaActual, calendar)
                    //layout.replaceComponent(capaActual2, loginForm)

                    //capaActual = loginForm

                }
            });
            MenuBar.MenuItem menuCerrarSesion = menubar.addItem("Cerrar sesion", null, new MenuBar.Command() {
                @Override
                void menuSelected(MenuBar.MenuItem menuItem) {
                    getSession().session.setAttribute(MyUI.USUARIO_LOGUEADO, null)
                    getUI().getPage().reload()

                }
            });
        }


        setContent(layout)
    }
}
