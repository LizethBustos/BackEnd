package RegistraduriaVotaciones.seguridadBE.Controladores;
import RegistraduriaVotaciones.seguridadBE.Modelo.Usuario;
import RegistraduriaVotaciones.seguridadBE.Modelo.Rol;
import RegistraduriaVotaciones.seguridadBE.Respositorios.RepositorioRol;
import RegistraduriaVotaciones.seguridadBE.Respositorios.RepositorioUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
@CrossOrigin
@RestController
@RequestMapping("/usuarios")
public class ControladorUsuario {
    public String convertirSHA256(String password) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
        byte[] hash = md.digest(password.getBytes());
        StringBuffer sb = new StringBuffer();
        for(byte b : hash) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    @Autowired
    private RepositorioUsuario miRepositorioUsuario;
    @Autowired
    private RepositorioRol miRepositorioRol;
    @GetMapping("")
    public List<Usuario> index(){
        return this.miRepositorioUsuario.findAll();//donde esta el findall?  en MongoRepository?
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Usuario create(@RequestBody Usuario infoUsuario){
        infoUsuario.setContraseña(convertirSHA256(infoUsuario.getContraseña()));
        Rol rolVotante=this.miRepositorioRol.findById("63725a526ee0d975568359aa").orElse(null);//llamamos al rol votante el cual ya esta creado en la base de datos
        infoUsuario.setRol(rolVotante);//Dar roll de votante por default a todos los usuarios
        return this.miRepositorioUsuario.save(infoUsuario);
    }
    @GetMapping("{id}")
    public Usuario show(@PathVariable String id){
        Usuario usuarioActual=this.miRepositorioUsuario
                .findById(id)
                .orElse(null);
        return usuarioActual;
    }
    @PutMapping("{id}")
    public Usuario update(@PathVariable String id,@RequestBody Usuario infoUsuario){
        Usuario usuarioActual=this.miRepositorioUsuario
                .findById(id)
                .orElse(null);
        if (usuarioActual!=null){
            usuarioActual.setSeudonimo(infoUsuario.getSeudonimo());
            usuarioActual.setCorreo(infoUsuario.getCorreo());
            usuarioActual.setContraseña(convertirSHA256(infoUsuario.getContraseña()));
            return this.miRepositorioUsuario.save(usuarioActual);
        }else {
            return null;
        }
    }
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("{id}")
    public void delete(@PathVariable String id){
        Usuario usuarioActual=this.miRepositorioUsuario
                .findById(id)
                .orElse(null);
        if(usuarioActual!=null){
            this.miRepositorioUsuario.delete(usuarioActual);
        }
    }

    /*** Relación (1 a n) entre rol y usuario//////////////////////////////////////////////////////////////////////////
     * @param id
     * @param id_rol
     * @return
     */
    @PutMapping("{id}/rol/{id_rol}")
    public Usuario asignarRolAUsuario(@PathVariable String id,@PathVariable String id_rol){
        Usuario usuarioActual=this.miRepositorioUsuario
                .findById(id)
                .orElse(null);
        Rol rolActual=this.miRepositorioRol
                .findById(id_rol)
                .orElse(null);
        if (usuarioActual!=null && rolActual!=null){
            usuarioActual.setRol(rolActual);
            return this.miRepositorioUsuario.save(usuarioActual);
        }else{
            return null;
        }

    }
    /** ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////  */

}
