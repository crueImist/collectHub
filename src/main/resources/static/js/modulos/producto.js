// Formulario de registro de producto: vista previa y validación de las imágenes
document.addEventListener('DOMContentLoaded', function () {
    const modal = document.getElementById('productoModal');
    if (!modal) {
        return;
    }
    const form = document.getElementById('productoForm');
    const imagenes = document.getElementById('productoImagenes');
    const previews = document.getElementById('productoPreviews');
    const MAX_IMAGENES = 5;
    const MAX_BYTES = 1000 * 1024; // 1 MB por imagen (límite por defecto de Spring)

    function limpiarPreviews() {
        previews.innerHTML = '';
    }

    imagenes.addEventListener('change', function () {
        limpiarPreviews();
        const archivos = Array.from(this.files);
        if (archivos.length > MAX_IMAGENES) {
            alert(this.dataset.errorMax);
            this.value = '';
            return;
        }
        if (archivos.some(function (a) { return a.size > MAX_BYTES; })) {
            alert(this.dataset.errorTamano);
            this.value = '';
            return;
        }
        archivos.forEach(function (archivo) {
            const lector = new FileReader();
            lector.onload = function (e) {
                const img = document.createElement('img');
                img.src = e.target.result;
                img.alt = archivo.name;
                img.height = 100;
                img.className = 'me-2 mb-2 rounded border';
                previews.appendChild(img);
            };
            lector.readAsDataURL(archivo);
        });
    });

    // Cada vez que se abre el modal, el formulario empieza limpio
    modal.addEventListener('show.bs.modal', function () {
        form.reset();
        limpiarPreviews();
    });
});
