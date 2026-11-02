// Carga los datos del empleado en el modal Crear o Editar (HU-05)
document.addEventListener('DOMContentLoaded', function () {
    const modal = document.getElementById('usuarioModal');
    if (!modal) {
        return;
    }
    const form = document.getElementById('usuarioForm');
    const header = document.getElementById('usuarioModalHeader');
    const titulo = document.getElementById('usuarioModalTitulo');
    const id = document.getElementById('usuarioId');
    const nombre = document.getElementById('usuarioNombre');
    const apellidos = document.getElementById('usuarioApellidos');
    const correo = document.getElementById('usuarioCorreo');
    const telefono = document.getElementById('usuarioTelefono');
    const contrasena = document.getElementById('usuarioContrasena');
    const ayudaCrear = document.getElementById('usuarioAyudaCrear');
    const ayudaEditar = document.getElementById('usuarioAyudaEditar');
    const roles = modal.querySelectorAll('.usuario-rol');

    modal.addEventListener('show.bs.modal', function (event) {
        const button = event.relatedTarget;
        if (!button) {
            return;
        }
        // Se limpia cualquier información de una apertura anterior
        form.reset();
        id.value = '';
        titulo.textContent = button.getAttribute('data-titulo') || '';
        header.classList.remove('bg-info', 'bg-warning', 'text-white');

        if (button.getAttribute('data-modo') === 'editar') {
            id.value = button.getAttribute('data-id') || '';
            nombre.value = button.getAttribute('data-nombre') || '';
            apellidos.value = button.getAttribute('data-apellidos') || '';
            correo.value = button.getAttribute('data-correo') || '';
            telefono.value = button.getAttribute('data-telefono') || '';
            const asignados = (button.getAttribute('data-roles') || '').split(',');
            roles.forEach(function (rol) {
                rol.checked = asignados.indexOf(rol.value) !== -1;
            });
            // Al modificar, la contraseña es opcional: vacía = se conserva la actual
            contrasena.required = false;
            ayudaCrear.classList.add('d-none');
            ayudaEditar.classList.remove('d-none');
            header.classList.add('bg-warning');
        } else {
            contrasena.required = true;
            ayudaCrear.classList.remove('d-none');
            ayudaEditar.classList.add('d-none');
            header.classList.add('bg-info', 'text-white');
        }
    });

    // Debe quedar al menos un rol marcado antes de enviar (el servidor lo vuelve a validar)
    form.addEventListener('submit', function (event) {
        const alguno = Array.from(roles).some(function (rol) { return rol.checked; });
        if (!alguno) {
            event.preventDefault();
            alert(form.dataset.errorRol);
        }
    });
});
