//Setting up the canvas
const canvas = document.querySelector('.nodes'); //Select the class on which to draw GUI
const width = canvas.width = window.innerWidth; //Window width, is the same as the viewport.
const height = canvas.height = window.innerHeight; //Window height, is the same as the viewport.
const ctx = canvas.getContext('2d'); //Selects in what context objects are drawn, could also be WebGL etc.

//Drawing in the canvas.
ctx.fillStyle = 'rgb(0, 0, 0)';
ctx.fillRect(0, 0, width, height);




