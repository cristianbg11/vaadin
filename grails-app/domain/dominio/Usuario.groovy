package dominio

class Usuario {

    String username
    String password
    String email
    String nombre
    String apellido
    static hasMany = [eventos: Evento]

    static constraints = {
        username blank: false, unique: true
        password blank: false
        email blank: false, unique: true, nullable: false, email: true
        nombre blank: false, nullable: false
        apellido blank: false, nullable: false
    }
}
