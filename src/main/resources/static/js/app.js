const themeToggle = document.getElementById('themeToggle');
const htmlEl = document.documentElement;
const sunIcon = document.querySelector('.sun-icon');
const moonIcon = document.querySelector('.moon-icon');

function updateThemeIcons() {
  const isDark = htmlEl.classList.contains('dark');
  if (sunIcon) sunIcon.classList.toggle('hidden', !isDark);
  if (moonIcon) moonIcon.classList.toggle('hidden', isDark);
}

if (themeToggle) {
  themeToggle.addEventListener('click', () => {
    htmlEl.classList.toggle('dark');
    localStorage.setItem('theme', htmlEl.classList.contains('dark') ? 'dark' : 'light');
    updateThemeIcons();
  });
}
updateThemeIcons();

const iaToggle = document.getElementById('iaWidgetToggle');
const iaPanel = document.getElementById('iaWidgetPanel');
const iaClose = document.getElementById('iaWidgetClose');
const iaInput = document.getElementById('iaChatInput');
const iaSend = document.getElementById('iaChatSend');
const iaMessages = document.getElementById('iaChatMessages');

function toggleIA() {
  if (!iaPanel) return;
  iaPanel.classList.toggle('hidden');
}
if (iaToggle) iaToggle.addEventListener('click', toggleIA);
if (iaClose) iaClose.addEventListener('click', () => iaPanel.classList.add('hidden'));

function addMsg(text, from = 'bot') {
  const div = document.createElement('div');
  div.className = 'text-sm p-2 rounded-lg ' + (from === 'user' ? 'bg-indigo-600 text-white ml-auto max-w-[85%]' : 'bg-gray-100 dark:bg-gray-800 mr-auto max-w-[85%]');
  div.textContent = text;
  iaMessages.appendChild(div);
  iaMessages.scrollTop = iaMessages.scrollHeight;
}

async function sendIA(msg) {
  try {
    addMsg(msg, 'user');
    const csrfToken = document.querySelector('meta[name="_csrf"]')?.content;
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content;
    const headers = { 'Content-Type': 'application/json' };
    if (csrfToken && csrfHeader) headers[csrfHeader] = csrfToken;
    const res = await fetch('/api/ia/chat', {
      method: 'POST',
      headers,
      body: JSON.stringify({ mensaje: msg })
    });
    const data = await res.json().catch(() => ({}));
    if (!res.ok) throw new Error(data.error || data.message || `La solicitud falló (${res.status})`);
    addMsg(data.respuesta || 'Sin respuesta', 'bot');
  } catch (error) {
    addMsg(error.message || 'No se pudo conectar con la IA', 'bot');
  }
}

if (iaSend) iaSend.addEventListener('click', () => {
  if (!iaInput) return;
  const v = iaInput.value.trim();
  if (!v) return;
  iaInput.value = '';
  sendIA(v);
});
if (iaInput) iaInput.addEventListener('keydown', (e) => {
  if (e.key === 'Enter') { e.preventDefault(); iaSend?.click(); }
});

document.querySelectorAll('.ia-sug').forEach(btn => {
  btn.addEventListener('click', () => {
    const sug = btn.dataset.sug;
    if (sug && iaInput) { iaInput.value = sug; iaInput.focus(); }
  });

  const mediaUploadForm = document.getElementById('mediaUploadForm');
  if (mediaUploadForm) {
    mediaUploadForm.addEventListener('submit', async event => {
      event.preventDefault();
      const result = document.getElementById('mediaUploadResult');
      const csrfToken = document.querySelector('meta[name="_csrf"]')?.content;
      const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content;
      const headers = {};
      if (csrfToken && csrfHeader) headers[csrfHeader] = csrfToken;
      try {
        const response = await fetch(mediaUploadForm.action, {
          method: 'POST',
          headers,
          body: new FormData(mediaUploadForm)
        });
        const data = await response.json();
        if (!response.ok) throw new Error(data.error || `La carga falló (${response.status})`);
        result.textContent = `Archivo subido: ${data.url}`;
        result.className = 'break-all text-sm text-green-600';
      } catch (error) {
        result.textContent = error.message || 'No se pudo subir el archivo';
        result.className = 'break-all text-sm text-red-600';
      }
    });
  }
});
