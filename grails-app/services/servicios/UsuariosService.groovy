package servicios

import dominio.Usuario
import grails.transaction.Transactional

@Transactional
class UsuariosService {
    /**
     * Metodo para recuperar un usuarios....
     * @param username
     * @param password
     * @return
     */
    public Usuario autenticarUsuario(String username, String password) {
        return Usuario.findByUsernameAndPassword(username, password);
    }
}