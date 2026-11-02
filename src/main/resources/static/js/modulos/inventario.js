// Carga el producto elegido en la ventana de ajuste de stock (HU-13)
document.addEventListener('DOMContentLoaded', function () {
    const modal = document.getElementById('ajusteModal');
    if (!modal) {
        return; // El rol CONSULTA no tiene ventana de ajuste
    }
    const form = document.getElementById('ajusteForm');
    const idProducto = document.getElementById('ajusteIdProducto');
    const producto = document.getElementById('ajusteProducto');
    const existencias = document.getElementById('ajusteExistencias');

    modal.addEventListener('show.bs.modal', function (event) {
        const button = event.relatedTarget;
        if (!button) {
            return;
        }
        form.reset();
        idProducto.value = button.getAttribute('data-id') || '';
        producto.textContent = button.getAttribute('data-nombre') || '';
        existencias.textContent = button.getAttribute('data-existencias') || '0';
    });
});
