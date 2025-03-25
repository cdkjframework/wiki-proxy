<template>
  <div class="editor">
    <ckeditor class="editor-content" :editor="state.editor" v-model="state.editorData" :config="state.editorConfig"
              @ready="onReady"></ckeditor>
  </div>
</template>

<script setup lang="ts">
import {onMounted, reactive} from 'vue';
import * as Editor from 'ckeditor5-custom-build/build/ckeditor';

let watchdog
const state = reactive({
  editor: Editor.Editor,
  editorData: '',
  editorConfig: {
    ckfinder: {
      uploadUrl: `/ckeditor/upload`,
      // 后端处理上传逻辑返回json数据,包括uploaded 上传的字节数 和url两个字段
    },
  }
})

const props = defineProps({
  editorData: null
})

defineExpose({
  getData() {
    return encodeURI(state.editorData);
  },
});

onMounted(() => {
  state.editorData = decodeURI(props.editorData)
})
const onReady = (editor: any) => {
  // Insert the toolbar before the editable area.
  editor.ui.getEditableElement().parentElement.insertBefore(
      editor.ui.view.toolbar.element,
      editor.ui.getEditableElement()
  );
}
</script>
