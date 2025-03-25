<template>
  <div class="editor-ui">
    <div class="centered">
      <div class="row">
        <div class="document-editor__toolbar"></div>
      </div>
      <div class="row row-editor">
        <div class="editor-container">
          <div class="editor">
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import {onMounted, reactive} from 'vue';

let watchdog

const state = reactive({
  editorData: null as any
})

onMounted(() => {
  watchdog = new CKSource.EditorWatchdog();
  watchdog.setCreator((element, config) => {
    return CKSource.Editor
      .create(element, config)
      .then(editor => {
        const toolbar = document.querySelector('.document-editor__toolbar');
        if (!toolbar.innerHTML) {
          toolbar?.appendChild(editor.ui.view.toolbar.element)
        }
        document.querySelector('.ck-toolbar')?.classList.add('ck-reset_all');
        return editor;
      })
  });

  watchdog.setDestructor(editor => {
    // Set a custom container for the toolbar.
    document.querySelector('.document-editor__toolbar').removeChild(editor.ui.view.toolbar.element);
    return editor.destroy();
  });

  watchdog.on('error', (error: any) => {
    console.error(error);
  });
  watchdog
    .create(document.querySelector('.editor'), {
      licenseKey: '',
    })
    .catch((error: any) => {
      console.error(error);
    });
  setInterval(() => {
    console.log('watchdog', watchdog.editor)
  }, 100)
})

</script>