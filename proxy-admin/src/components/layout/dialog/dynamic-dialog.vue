<template>
  <div class="dynamic-dialog">
    <el-dialog
        v-model="state.dialogVisible"
        :top="state.top"
        :before-close="handleClose"
        :close-on-click-modal="false"
        :width="state.width"
        :title="state.title"
    >
      <slot name="dialog"></slot>
      <div
          class="dynamic-dialog-button"
          v-if="state.button.cancel || state.button.confirm"
      >
        <el-button v-if="state.button.cancel" @click="handleClose">{{
            state.button.cancelText
          }}
        </el-button>
        <el-button v-preventReClick
                   v-if="state.button.confirm"
                   @click="handleConfirm"
                   type="Primary"
        >{{ state.button.confirmText }}
        </el-button
        >
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import {onMounted, reactive, watch} from "vue";

const emits = defineEmits<{ (e: "click", data: object): void }>();

const state = reactive({
  dialogVisible: false as any,
  title: "弹窗" as any,
  width: "100%" as any,
  loading: false as any,
  top: "0vh" as any,
  button: {
    cancel: true as any,
    cancelText: "取消" as any,
    confirm: true as any,
    confirmText: "保存" as any,
  },
});

const props = defineProps({
  dialogVisible: {
    type: Boolean,
    default: false
  },
  title: {
    type: String,
    default: () => ''
  },
  width: {
    type: String,
    default: () => '100%'
  },
  loading: {
    type: Boolean,
    default: false
  },
  top: {
    type: String,
    default: () => ''
  },
  button: {
    default: {
      cancel: true,
      cancelText: "取消",
      confirm: true,
      confirmText: "保存",
    }
  },
});

watch(
    () => [props.dialogVisible, props.title, props.loading, props.width],
    ([dialogVisible, title, loading, width]) => {
      state.title = title || state.title;
      state.dialogVisible = dialogVisible || false;
      state.loading = loading || false;
      state.width = width;
    }
);

onMounted(() => {
  //console.log('onMounted', props)
  state.width = props.width;
  state.title = props.title || state.title;
  state.dialogVisible = props.dialogVisible || false;
  state.loading = props.loading || false;
  state.top = props.top || "0vh";
  state.button = Object.assign(state.button, props.button);
});

const handleClose = () => {
  emits("click", {type: "dialogClose"});
};
const handleConfirm = () => {
  emits("click", {type: "dialogConfirm"});
};
</script>
