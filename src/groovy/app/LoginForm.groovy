package app

import com.vaadin.grails.Grails
import com.vaadin.server.VaadinSession
import com.vaadin.ui.*
import servicios.UsuariosService

/**
 * Created by cristian on 13/11/2020.
 */
class LoginForm extends VerticalLayout {

    public LoginForm()
    {
        Label f = new Label("Login")
        f.setStyleName("h1")
        addComponent(f)

        FormLayout formLayout=new FormLayout();

        TextField txtNomnbre=new TextField("Usuario: ");
        txtNomnbre.setRequired(true);

        PasswordField txtPass=new PasswordField("Contrasena: ");
        txtPass.setRequired(true);

        Button btnAceptar=new Button("Aceptar");
        btnAceptar.addClickListener(new Button.ClickListener() {
            @Override
            void buttonClick(Button.ClickEvent clickEvent) {

                if(txtNomnbre.isValid() && txtPass.isValid()){
                    //Validando desde un servicios.
                    def usuario =Grails.get(UsuariosService).autenticarUsuario(txtNomnbre.value, txtPass.value);
                    if(usuario){
                        //Agregar un elemento a la sesion..
                        getSession().session.setAttribute(MyUI.USUARIO_LOGUEADO, usuario);
                       // setContent(new VlPanelPrincipal());
                        //Rediredccionar....
                        getUI().getPage().reload()


                        Notification.show("Autenticado, usuario ${usuario.nombre}",Notification.Type.TRAY_NOTIFICATION)
                    } else{
                        Notification.show("Usuario o contraseña incorrecta",Notification.Type.ERROR_MESSAGE)
                    }
                } else{
                    Notification.show("Campos no validos...",Notification.Type.WARNING_MESSAGE)
                }
            }
        })

        formLayout.addComponent(txtNomnbre)
        formLayout.addComponent(txtPass)
        formLayout.addComponent(btnAceptar)

        addComponent(formLayout)
    }
}
