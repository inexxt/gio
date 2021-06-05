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

function applyTheme() {
    let theme = matchMedia("(prefers-color-scheme: dark)").matches ? "dark" : "light";
    document.documentElement.setAttribute("theme", theme);
}

matchMedia("(prefers-color-scheme: dark)").addEventListener("change", applyTheme);

applyTheme();


