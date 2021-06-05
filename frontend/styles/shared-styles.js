// eagerly import theme styles so as we can override them
import '@vaadin/vaadin-lumo-styles/all-imports';

const $_documentContainer = document.createElement('template');

$_documentContainer.innerHTML = `
<custom-style>
<style include='lumo-badge'>
    
</style>
</custom-style>


`;

document.head.appendChild($_documentContainer.content);

window.changeColor = function() {
    if (typeof changeColor.color == 'undefined')
        changeColor.color = 'dark';
    else if (changeColor.color == 'dark')
        changeColor.color = 'light';
    else
        changeColor.color = 'dark';

    document.documentElement.setAttribute("theme", changeColor.color);
}


