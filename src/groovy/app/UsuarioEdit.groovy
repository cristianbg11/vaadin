package app

import com.vaadin.grails.Grails
import com.vaadin.server.VaadinSession
import com.vaadin.ui.Button
import com.vaadin.ui.CheckBox
import com.vaadin.ui.FormLayout
import com.vaadin.ui.Label
import com.vaadin.ui.Notification
import com.vaadin.ui.Panel
import com.vaadin.ui.PasswordField
import com.vaadin.ui.TextField
import com.vaadin.ui.VerticalLayout
import dominio.Usuario
import servicios.UsuariosService

/**
 * Created by cristian on 13/11/2020.
 */
class UsuarioEdit extends VerticalLayout{
    public UsuarioEdit(Usuario usuario)
    {
        def labelActual
        labelActual = new Label("Editando Usuario - " + usuario.nombre)

        labelActual.setStyleName("h1")
        addComponent(labelActual)
        FormLayout formLayout=new FormLayout();

        TextField txtUsuario=new TextField("Usuario: ");
        txtUsuario.setRequired(true);

        TextField txtNombre=new TextField("Nombre: ");
        txtNombre.setRequired(true);

        TextField txtApellido =new TextField("Apellido: ");

        TextField txtEmail =new TextField("Email: ");

        PasswordField pwdPassword =new PasswordField("Contrasena: ");



        if(usuario !=null)
        {
            txtNombre.value = usuario.nombre
            txtUsuario.value = usuario.username
            txtApellido.value = usuario.apellido
            txtEmail.value = usuario.email
            pwdPassword.value = usuario.password
        }


        Button btnAceptar=new Button("Aceptar");
        btnAceptar.addClickListener(new Button.ClickListener() {
            @Override
            void buttonClick(Button.ClickEvent clickEvent) {

                def user = Usuario.findById(usuario.id)
                user.setNombre(txtNombre.value)
                user.setApellido(txtApellido.value)
                user.setUsername(txtUsuario.value)
                user.setEmail(txtEmail.value)

                if(pwdPassword != null)
                    user.setPassword(pwdPassword.value)
                user.save flush:true

                if(user.hasErrors())
                {
                    println(user.errors)
                }
                if(usuario.username!=user.username || usuario.password!=user.password)
                {
                    getSession().session.setAttribute(MyUI.USUARIO_LOGUEADO, null)
                }
                else
                    getSession().session.setAttribute(MyUI.USUARIO_LOGUEADO, Grails.get(UsuariosService).autenticarUsuario(user.username, user.password));
                    getUI().getPage().reload()
            }
        })


        formLayout.addComponent(txtUsuario)
        formLayout.addComponent(txtNombre)
        formLayout.addComponent(txtEmail)

        formLayout.addComponent(txtApellido)
        formLayout.addComponent(pwdPassword)
        formLayout.addComponent(btnAceptar)

        addComponent(formLayout)
    }
}