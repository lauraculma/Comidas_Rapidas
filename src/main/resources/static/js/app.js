
document.addEventListener('DOMContentLoaded',()=>{
 const side=document.querySelector('.sidebar'),toggle=document.querySelector('[data-sidebar-toggle]');
 toggle?.addEventListener('click',()=>side?.classList.toggle('open'));
 document.querySelectorAll('[data-filter-table]').forEach(input=>{
   const table=document.querySelector(input.dataset.filterTable); if(!table)return;
   input.addEventListener('input',()=>table.querySelectorAll('tbody tr').forEach(r=>r.style.display=r.innerText.toLowerCase().includes(input.value.toLowerCase())?'':'none'));
 });
 document.querySelectorAll('[data-confirm]').forEach(el=>el.addEventListener('click',e=>{if(!confirm(el.dataset.confirm||'¿Confirmar?'))e.preventDefault()}));
});
