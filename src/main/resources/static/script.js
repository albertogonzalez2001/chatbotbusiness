// Estado de la aplicación
// sessionId se obtiene del servidor al cargar la página
// y se reutiliza en todas las peticiones de esta sesión
let sessionId = null;

// Referencias al DOM
// Las capturamos una vez al inicio — no las buscamos en cada evento
const chatMessages = document.getElementById('chatMessages');
const userInput    = document.getElementById('userInput');
const sendButton   = document.getElementById('sendButton');

// Inicialización
// Al cargar la página, pedimos un sessionId al servidor
// y mostramos un mensaje de bienvenida
async function init() {
    try {
        const res  = await fetch('/api/chat/session');
        const data = await res.json();
        sessionId  = data.sessionId;

        appendMessage('assistant',
            '¡Hola! Soy el asistente virtual de Café González. ' +
            '¿En qué puedo ayudarte?'
        );
    } catch (error) {
        appendMessage('assistant',
            'Error al conectar con el servidor. Inténtalo más tarde.'
        );
    }
}

// Enviar mensaje
// Recoge el input, lo valida, lo muestra en pantalla
// y llama a la API
async function sendMessage() {
    const text = userInput.value.trim();

    // Validación: no enviar mensajes vacíos
    if (!text || !sessionId) return;

    // Limpiar input y deshabilitar botón mientras se espera respuesta
    userInput.value = '';
    setInputEnabled(false);

    // Mostrar el mensaje del usuario en el chat
    appendMessage('user', text);

    // Mostrar indicador de carga mientras el LLM procesa
    const loadingEl = appendLoading();

    try {
        const res = await fetch('/api/chat', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ sessionId, message: text })
        });

        const data = await res.json();

        // Eliminar indicador de carga y mostrar respuesta real
        loadingEl.remove();
        appendMessage('assistant', data.response ?? data.error);

    } catch (error) {
        loadingEl.remove();
        appendMessage('assistant', 'Error al conectar con el servidor.');
    } finally {
        // Siempre rehabilitar el input, haya error o no
        setInputEnabled(true);
        userInput.focus();
    }
}

// Renderizar un mensaje en el DOM
// role: 'user' | 'assistant'
// Devuelve el elemento creado por si necesitamos referenciarlo
function appendMessage(role, text) {
    const div = document.createElement('div');
    div.classList.add('message', `message--${role}`);
    div.textContent = text;
    chatMessages.appendChild(div);

    // Auto-scroll al último mensaje
    chatMessages.scrollTop = chatMessages.scrollHeight;

    return div;
}

// Indicador de carga
// Muestra tres puntos animados mientras el LLM responde
function appendLoading() {
    const div = document.createElement('div');
    div.classList.add('message', 'message--loading');
    div.innerHTML = `
        <div class="loading-dots">
            <span></span><span></span><span></span>
        </div>
    `;
    chatMessages.appendChild(div);
    chatMessages.scrollTop = chatMessages.scrollHeight;
    return div;
}

// Habilitar / deshabilitar input
// Evita que el usuario envíe múltiples mensajes mientras
// el LLM está procesando el anterior
function setInputEnabled(enabled) {
    userInput.disabled  = !enabled;
    sendButton.disabled = !enabled;
}

// Eventos
// Click en botón
sendButton.addEventListener('click', sendMessage);

// Enter en el input — comportamiento estándar de cualquier chat
userInput.addEventListener('keydown', (e) => {
    if (e.key === 'Enter') sendMessage();
});

// Arranque
init();

