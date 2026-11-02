// Carga los datos de la categoría en el modal Agregar o Editar
document.addEventListener('DOMContentLoaded', function () {
    const modal = document.getElementById('categoriaModal');
    if (!modal) {
        return;
    }
    const form = document.getElementById('categoriaForm');
    const header = document.getElementById('categoriaModalHeader');
    const titulo = document.getElementById('categoriaModalTitulo');
    const id = document.getElementById('categoriaId');
    const descripcion = document.getElementById('categoriaDescripcion');
    const activo = document.getElementById('categoriaActivo');
    const imagenFile = document.getElementById('categoriaImagenFile');
    const preview = document.getElementById('categoriaPreview');
    const MAX_BYTES = 5 * 1024 * 1024; // 5 MB, igual que valida el servidor

    function limpiarPreview() {
        preview.removeAttribute('src');
        preview.classList.add('d-none');
    }

    function mostrarPreview(ruta) {
        if (ruta) {
            preview.src = ruta;
            preview.classList.remove('d-none');
        } else {
            limpiarPreview();
        }
    }

    // Vista previa de la imagen que se acaba de elegir
    imagenFile.addEventListener('change', function () {
        const archivo = this.files && this.files[0];
        if (!archivo) {
            limpiarPreview();
            return;
        }
        if (archivo.size > MAX_BYTES) {
            alert(this.dataset.errorTamano);
            this.value = '';
            limpiarPreview();
            return;
        }
        const lector = new FileReader();
        lector.onload = function (e) {
            mostrarPreview(e.target.result);
        };
        lector.readAsDataURL(archivo);
    });

    modal.addEventListener('show.bs.modal', function (event) {
        const button = event.relatedTarget;
        if (!button) {
            return;
        }
        // Se limpia cualquier información de una apertura anterior
        form.reset();
        id.value = '';
        imagenFile.value = '';
        limpiarPreview();
        titulo.textContent = button.getAttribute('data-titulo') || '';
        header.classList.remove('bg-info', 'bg-warning', 'text-white');

        if (button.getAttribute('data-modo') === 'editar') {
            id.value = button.getAttribute('data-id') || '';
            descripcion.value = button.getAttribute('data-descripcion') || '';
            activo.checked = button.getAttribute('data-activo') === 'true';
            mostrarPreview(button.getAttribute('data-imagen'));
            header.classList.add('bg-warning');
        } else {
            // Una categoría nueva inicia activa
            activo.checked = true;
            header.classList.add('bg-info', 'text-white');
        }
    });
});
