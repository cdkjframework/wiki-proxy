<template>
  <div class="form">
    <el-form
        ref="ruleFormRef"
        :inline="state.type !== 'search'"
        :model="state.rulesForm"
        :rules="state.rules"
        :label-width="state.labelWidth"
        :class="
        state.type !== 'search'
          ? 'dynamic-form'
          : 'dynamic-form dynamic-form-search'
      "
        @submit.prevent="handleSubmit"
        status-icon
    >
      <el-card
          ref="cardRef"
          :class="state.type !== 'search' ? '' : state.cardClass"
          v-for="(forms, index) in state.dataForm"
          :key="'card-' + index"
      >
        <template #header v-if="forms.label">
          <div v-if="forms.label" class="card-header" :key="index">
            <span>{{ forms.label }}</span>
          </div>
        </template>
        <div class="card-form">
          <el-row
              v-for="(row, rowIdx) in forms.card"
              :gutter="row.gutter || 24"
              :key="'row-' + index + '-' + rowIdx"
          >
            <el-col
                v-for="(col, colIdx) in row.line"
                :span="col.span || 6"
                :key="'col-' + index + '-' + rowIdx + '-' + colIdx"
            >
              <el-form-item
                  :label="col.label"
                  :align="col.align || 'right'"
                  :prop="col.key"
                  :label-width="col.width || '120px'"
                  v-if="col.type === 'checkbox'"
              >
                <el-checkbox-group
                    v-model="state.rulesForm[col.key]"
                    :disabled="col.disabled"
                >
                  <el-checkbox
                      v-for="(ckBox, boxIdx) in col.options"
                      :label="ckBox.value"
                      :key="
                      'col-' +
                      index +
                      '-' +
                      rowIdx +
                      '-' +
                      colIdx +
                      '-' +
                      boxIdx
                    "
                  >
                    {{ ckBox.label }}
                  </el-checkbox>
                </el-checkbox-group>
                <span class="explain" v-if="col.explain" v-html="col.explain"></span>
              </el-form-item>
              <el-form-item
                  :label="col.label"
                  :align="col.align || 'right'"
                  :prop="col.key"
                  :label-width="col.width || '120px'"
                  v-else-if="col.type === 'radio'"
              >
                <el-radio-group
                    v-model="state.rulesForm[col.key]"
                    :disabled="col.disabled"
                >
                  <el-radio
                      v-for="(ckBox, boxIdx) in col.options"
                      :label="ckBox.value"
                      :key="
                      'col-' +
                      index +
                      '-' +
                      rowIdx +
                      '-' +
                      colIdx +
                      '-' +
                      boxIdx
                    "
                  >
                    {{ ckBox.label }}
                  </el-radio>
                </el-radio-group>
                <span class="explain" v-if="col.explain" v-html="col.explain"></span>
              </el-form-item>
              <el-form-item
                  :label="col.label"
                  :align="col.align || 'right'"
                  :prop="col.key"
                  :label-width="col.width || '120px'"
                  v-else-if="col.type === 'date'"
              >
                <el-date-picker
                    v-model="state.rulesForm[col.key]"
                    :disabled="col.disabled"
                    type="date"
                    :placeholder="col.placeholder"
                />
                <span class="explain" v-if="col.explain" v-html="col.explain"></span>
              </el-form-item>
              <el-form-item
                  :label="col.label"
                  :align="col.align || 'right'"
                  :prop="col.key"
                  :label-width="col.width || '120px'"
                  v-else-if="col.type === 'daterange'"
              >
                <el-date-picker
                    v-model="state.rulesForm[col.key]"
                    type="daterange"
                    :disabled="col.disabled"
                    :range-separator="col.separator || '至'"
                    :start-placeholder="col.startPlaceholder || '开始'"
                    :end-placeholder="col.endPlaceholder || '结束'"
                />
                <span class="explain" v-if="col.explain" v-html="col.explain"></span>
              </el-form-item>
              <el-form-item
                  :label="col.label"
                  :align="col.align || 'right'"
                  :prop="col.key"
                  :label-width="col.width || '120px'"
                  v-else-if="col.type === 'datetime'"
              >
                <el-date-picker
                    v-model="state.rulesForm[col.key]"
                    :disabled="col.disabled"
                    type="datetime"
                    :placeholder="col.placeholder"
                />
                <span class="explain" v-if="col.explain" v-html="col.explain"></span>
              </el-form-item>
              <el-form-item
                  :label="col.label"
                  :align="col.align || 'right'"
                  :prop="col.key"
                  :label-width="col.width || '120px'"
                  v-else-if="col.type === 'datetimerange'"
              >
                <el-date-picker
                    v-model="state.rulesForm[col.key]"
                    type="datetimerange"
                    :disabled="col.disabled"
                    :range-separator="col.separator || '至'"
                    :start-placeholder="col.startPlaceholder || '开始'"
                    :end-placeholder="col.endPlaceholder || '结束'"
                />
                <span class="explain" v-if="col.explain" v-html="col.explain"></span>
              </el-form-item>
              <el-form-item
                  :label="col.label"
                  :align="col.align || 'right'"
                  :prop="col.key"
                  :label-width="col.width || '120px'"
                  v-else-if="col.type === 'timepicker'"
              >
                <el-time-picker
                    v-model="state.rulesForm[col.key]"
                    :disabled="col.disabled"
                    :placeholder="col.placeholder"
                />
                <span class="explain" v-if="col.explain" v-html="col.explain"></span>
              </el-form-item>
              <el-form-item
                  :label="col.label"
                  :align="col.align || 'right'"
                  :prop="col.key"
                  :label-width="col.width || '120px'"
                  v-else-if="col.type === 'timerangepicker'"
              >
                <el-time-picker
                    v-model="state.rulesForm[col.key]"
                    is-range
                    :disabled="col.disabled"
                    :range-separator="col.separator || '至'"
                    :start-placeholder="col.startPlaceholder || '开始'"
                    :end-placeholder="col.endPlaceholder || '结束'"
                />
                <span class="explain" v-if="col.explain" v-html="col.explain"></span>
              </el-form-item>
              <el-form-item
                  :label="col.label"
                  :align="col.align || 'right'"
                  :prop="col.key"
                  :label-width="col.width || '120px'"
                  v-else-if="col.type === 'selecttime'"
              >
                <el-time-select
                    :clearable="col.clearable"
                    filterable
                    v-model="state.rulesForm[col.key]"
                    :disabled="col.disabled"
                    :start="col.start || '00:00'"
                    :step="col.step || '00:30'"
                    :end="col.end || '23:59'"
                    :placeholder="col.placeholder"
                    :format="col.format || 'hh:mm'"
                />
                <span class="explain" v-if="col.explain" v-html="col.explain"></span>
              </el-form-item>
              <el-form-item
                  :label="col.label"
                  :align="col.align || 'right'"
                  :prop="col.key"
                  :label-width="col.width || '120px'"
                  v-else-if="col.type === 'cascader'"
              >
                <el-cascader
                    v-model="state.rulesForm[col.key]"
                    :clearable="col.clearable"
                    filterable
                    :props="col.props"
                    :disabled="col.disabled"
                    :options="col.options"
                    @change="
                    (e:any) => {
                      onChange(e, col.key);
                    }
                  "
                />
                <span class="explain" v-if="col.explain" v-html="col.explain"></span>
              </el-form-item>
              <el-form-item
                  :label="col.label"
                  :align="col.align || 'right'"
                  :prop="col.key"
                  :label-width="col.width || '120px'"
                  v-else-if="col.type === 'switch'"
              >
                <el-switch
                    v-model="state.rulesForm[col.key]"
                    :disabled="col.disabled"
                    :inline-prompt="col.inline || false"
                    :active-text="col.activeText || '是'"
                    :inactive-text="col.inactiveText || '否'"
                    :active-value="col.activeValue || 1"
                    :inactive-value="col.inactiveValue || 0"
                />
                <span class="explain" v-if="col.explain" v-html="col.explain"></span>
              </el-form-item>
              <el-form-item
                  :label="col.label"
                  :align="col.align || 'right'"
                  :prop="col.key"
                  :label-width="col.width || '120px'"
                  v-else-if="col.type === 'rate'"
              >
                <el-rate
                    v-model="state.rulesForm[col.key]"
                    :colors="col.colors"
                    :disabled="col.disabled"
                />
                <span class="explain" v-if="col.explain" v-html="col.explain"></span>
              </el-form-item>
              <el-form-item
                  :label="col.label"
                  :align="col.align || 'right'"
                  :prop="col.key"
                  style="display: flex;"
                  :label-width="col.width || '120px'"
                  v-else-if="col.type === 'select'"
              >
                <el-select
                    :clearable="col.clearable"
                    filterable
                    v-model="state.rulesForm[col.key]"
                    :disabled="col.disabled"
                    :multiple="col.multiple"
                    @change="
                    (e:any) => {
                      onChange(e, col.key);
                    }
                  "
                >
                  <el-option
                      v-for="(opt, optIdx) in col.options"
                      :label="opt.label"
                      :value="opt.value"
                      :disabled="opt.disabled"
                      :key="
                      'col-' +
                      index +
                      '-' +
                      rowIdx +
                      '-' +
                      colIdx +
                      '-' +
                      optIdx
                    "
                  >
                  </el-option>
                </el-select>
                <span class="explain" v-if="col.explain" v-html="col.explain"></span>
              </el-form-item>
              <el-form-item
                  :label="col.label"
                  :align="col.align || 'right'"
                  :prop="col.key"
                  :label-width="col.width || '120px'"
                  style="width: 100%"
                  v-else-if="col.type === 'text'"
              >
                <el-input
                    :type="col.type"
                    @dblclick="(e)=>{
                    onChange(e,col.key)
                  }"
                    v-model="state.rulesForm[col.key]"
                    :placeholder="col.placeholder"
                    :disabled="col.disabled"
                    :style="col.style"
                >
                  <template #append v-if="col.append">
                    <el-button
                        v-if="!col.template||col.template===0"
                        v-preventReClick
                        :icon="col.icon||search"
                        @click="
                        () => {
                          onChange(state.rulesForm[col.key],col.key);
                        }
                      "
                    />
                    <span v-if="col.template==1">{{ col.text }}</span>
                  </template>
                  <template #prepend v-if="col.prepend">
                    <el-button
                        v-if="!col.template||col.template===0"
                        v-preventReClick
                        :icon="col.icon||search"
                        @click="
                        () => {
                          onChange(state.rulesForm[col.key],col.key);
                        }
                      "
                    />
                    <span v-if="col.template==1">{{ col.text }}</span>
                  </template>
                </el-input>
                <span class="explain" v-if="col.explain" v-html="col.explain"></span>
              </el-form-item>
              <el-form-item
                  :label="col.label"
                  :align="col.align || 'right'"
                  :prop="col.key"
                  :label-width="col.width || '120px'"
                  v-else-if="col.type === 'textarea'"
                  style="width: 100%"
              >
                <el-input
                    :type="col.type"
                    show-word-limit
                    v-model="state.rulesForm[col.key]"
                    :placeholder="col.placeholder"
                    :disabled="col.disabled"
                    :style="col.style"
                />
                <span class="explain" v-if="col.explain" v-html="col.explain"></span>
              </el-form-item>
              <el-form-item
                  :label="col.label"
                  :align="col.align || 'right'"
                  :prop="col.key"
                  :label-width="col.width || '120px'"
                  v-else-if="col.type === 'number'"
                  style="width: 100%"
              >
                <el-input-number
                    :type="col.type"
                    show-word-limit
                    :min="col.min"
                    :max="col.max"
                    v-model="state.rulesForm[col.key]"
                    :placeholder="col.placeholder"
                    :disabled="col.disabled"
                    :style="col.style"
                />
                <span class="explain" v-if="col.explain" v-html="col.explain"></span>
              </el-form-item>
              <el-form-item
                  :label="col.label"
                  :align="col.align || 'right'"
                  :prop="col.key"
                  :label-width="col.width || '120px'"
                  v-else-if="col.type === 'editor'"
                  style="width: 100%"
              >
                <div class="editor">
                  <ckeditor class="editor-content" :editor="state.editor" v-model="state.rulesForm[col.key]"
                            :config="state.editorConfig"
                            @ready="onReady"></ckeditor>
                </div>
                <span class="explain" v-if="col.explain" v-html="col.explain"></span>
              </el-form-item>
              <el-form-item
                  :label="col.label"
                  :align="col.align || 'right'"
                  :prop="col.key"
                  :label-width="col.width || '120px'"
                  v-else-if="col.type === 'image'"
              >
                <el-upload
                    v-model="state.rulesForm[col.key]"
                    class="avatar-uploader"
                    :action="col.action"
                    :data="col.data"
                    :disabled="col.disabled"
                    :headers="col.headers"
                    :show-file-list="false"
                    :on-success="(response: any, uploadFile: UploadFile, uploadFiles: UploadFiles)=>{
                      handleSuccess(response,uploadFile,uploadFiles,col.key)
                    }"
                    :before-upload="(rowFile:any) => {
                      beforeUploadFile(rowFile, col.suffix || ['image/jpeg','image/png','image/jpg','image/gif'], col.size,col.key);
                    }"
                >
                  <img v-if="imageUrl" :src="imageUrl" class="avatar"/>
                  <el-icon v-else class="avatar-uploader-icon">
                    <Plus/>
                  </el-icon>
                </el-upload>
                <span class="explain" v-if="col.explain" v-html="col.explain"></span>
              </el-form-item>
              <el-form-item
                  :label="col.label"
                  :align="col.align || 'right'"
                  :prop="col.key"
                  :label-width="col.width || '120px'"
                  v-else-if="col.type === 'images'"
              >
                <el-upload
                    v-model:file-list="col.value"
                    :action="col.action"
                    :data="col.data"
                    :headers="col.headers"
                    :disabled="col.disabled"
                    :limit="col.limit || 3"
                    list-type="picture-card"
                    :on-success="(response: any, uploadFile: UploadFile, uploadFiles: UploadFiles)=>{
                      handleSuccess(response,uploadFile,uploadFiles,col.key)
                    }"
                    :before-upload="
                    (rowFile :any) => {
                      beforeUploadFile(rowFile,col.suffix || ['image/jpeg','image/png','image/jpg','image/gif'],col.size,col.key)
                    }"
                    :on-preview="handlePictureCardPreview"
                    :on-remove="
                    (uploadFile:any, uploadFiles:any) => {
                      handleRemove(uploadFile, uploadFiles, col.value)
                    }"
                >
                  <el-icon>
                    <Plus/>
                  </el-icon>
                </el-upload>
                <span class="explain" v-if="col.explain" v-html="col.explain"></span>
                <el-dialog v-model="dialogVisible">
                  <img w-full :src="dialogImageUrl" alt="图片预览"/>
                </el-dialog>
              </el-form-item>
              <el-form-item
                  :label="col.label"
                  :align="col.align || 'right'"
                  :prop="col.key"
                  :label-width="col.width || '120px'"
                  v-else-if="col.type === 'file'"
              >
                <el-upload
                    v-model:file-list="col.value"
                    class="upload-demo"
                    :action="col.action"
                    :data="col.data"
                    :disabled="col.disabled"
                    :headers="col.headers"
                    :limit="col.limit || 3"
                    :on-success="(response: any, uploadFile: UploadFile, uploadFiles: UploadFiles)=>{
                      handleSuccess(response,uploadFile,uploadFiles,col.key)
                    }"
                    :before-upload="
                    (rowFile:any) => {
                      beforeUploadFile(
                        rowFile,
                        col.suffix || [
                          'application/doc',
                          'application/docx',
                          'application/xls',
                          'application/xlsx',
                          'application/pdf',
                          'application/ppt',
                          'application/pptx',
                          'application/mp3',
                          'application/amr',
                          'application/mp4',
                          'image/jpeg',
                          'image/png',
                          'image/jpg',
                          'image/gif',
                        ],
                        col.size,col.key
                      );
                    }
                  "
                    :on-remove="
                    (uploadFile:any, uploadFiles:any) => {
                      handleRemove(uploadFile, uploadFiles, col.value);
                    }
                  "
                >
                  <el-button type="primary">上传文件</el-button>
                  <template #tip>
                    <div class="el-upload__tip">
                      {{ row.remark }}
                    </div>
                  </template>
                </el-upload>
                <span class="explain" v-if="col.explain" v-html="col.explain"></span>
              </el-form-item>
              <el-form-item
                  :label="col.label"
                  :align="col.align || 'right'"
                  :prop="col.key"
                  :label-width="col.width || '120px'"
                  v-else
              >
                <span>{{
                    formatterValue(state.rulesForm[col.key], col.value)
                  }}</span>
                <span class="explain" v-if="col.explain" v-html="col.explain"></span>
              </el-form-item>
            </el-col>
          </el-row>
        </div>
        <el-form-item
            :class="state.searchButton"
            v-if="state.type === 'search'"
        >
          <el-button type="primary" plain @click="restForm">重置</el-button>
          <el-button v-preventReClick type="primary" @click="handleSubmit">查询</el-button>
          <iconDow
              style="display: none"
              v-if="state.searchDow"
              @click="
              () => {
                searchStyle(0);
              }
            "
              ref="iconDowRef"
              class="search"
          />
          <iconUp
              v-if="state.searchUp"
              @click="
              () => {
                searchStyle(1);
              }
            "
              ref="iconUpRef"
              class="search"
          />
        </el-form-item>
      </el-card>
      <el-form-item class="form-item-button" v-if="state.type === 'form'">
        <el-button type="info" @click="restForm">重置</el-button>
        <el-button v-preventReClick type="submit" @click="handleSubmit">保存</el-button>
      </el-form-item>
      <el-form-item class="form-item-button" v-if="state.see">
        <el-button type="info" @click="handleClose">关闭</el-button>
      </el-form-item>
      <el-form-item class="form-item-button" v-if="state.type === 'custom'">
        <el-button v-for="btn in state.buttons" v-preventReClick :type="btn.type"
                   @click="()=>{handleSubmit(btn.primary)}">{{ btn.label }}
        </el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script lang="ts" setup>
import {onMounted, reactive, ref, watch,} from "vue";
import type {FormInstance, UploadFile, UploadFiles, UploadProps} from "element-plus";
import {ElMessage} from "element-plus";
import {Search} from "@element-plus/icons-vue";
import {
  AccessibilityHelp,
  Alignment,
  Autoformat,
  AutoImage,
  AutoLink,
  Autosave,
  BalloonToolbar,
  Base64UploadAdapter,
  BlockQuote,
  Bold,
  Code,
  CodeBlock,
  DecoupledEditor,
  Essentials,
  FindAndReplace,
  FontBackgroundColor,
  FontColor,
  FontFamily,
  FontSize,
  GeneralHtmlSupport,
  Heading,
  Highlight,
  HorizontalLine,
  HtmlComment,
  HtmlEmbed,
  ImageBlock,
  ImageCaption,
  ImageInline,
  ImageInsert,
  ImageInsertViaUrl,
  ImageResize,
  ImageStyle,
  ImageTextAlternative,
  ImageToolbar,
  ImageUpload,
  Indent,
  IndentBlock,
  Italic,
  Link,
  LinkImage,
  List,
  ListProperties,
  Markdown,
  MediaEmbed,
  Mention,
  PageBreak,
  Paragraph,
  PasteFromMarkdownExperimental,
  PasteFromOffice,
  RemoveFormat,
  SelectAll,
  ShowBlocks,
  SpecialCharacters,
  SpecialCharactersArrows,
  SpecialCharactersCurrency,
  SpecialCharactersEssentials,
  SpecialCharactersLatin,
  SpecialCharactersMathematical,
  SpecialCharactersText,
  Strikethrough,
  Style,
  Subscript,
  Superscript,
  Table,
  TableCaption,
  TableCellProperties,
  TableColumnResize,
  TableProperties,
  TableToolbar,
  TextPartLanguage,
  TextTransformation,
  TodoList,
  Underline,
  Undo
} from 'ckeditor5';
import translations from 'ckeditor5/translations/zh-cn.js';
import {Ckeditor} from '@ckeditor/ckeditor5-vue';
import {CaretDownOutlined, CaretUpOutlined} from "@ant-design/icons-vue";
import moment from "moment";
import {uploadFilesApi} from "@/api/file";

import 'ckeditor5/ckeditor5.css';

let loaders: any

const iconDow = CaretDownOutlined;
const iconUp = CaretUpOutlined;
const search = Search;
const ruleFormRef = ref<FormInstance>();
const ckeditorRef = ref();
const dialogVisible = ref(false);

// 图片展示
const dialogImageUrl = ref("");
const imageUrl = ref("");

const state = reactive({
  rulesForm: {} as any,
  dataForm: [] as any,
  rules: {} as any,
  type: '' as string,
  see: false,
  searchUp: false,
  searchDow: true,
  cardClass: "card-search",
  searchButton: "search-item-button",
  labelWidth: null as any,
  buttons: [] as any,
  editor: DecoupledEditor,
  editorConfig: {
    translations: [translations],
    plugins: [AccessibilityHelp,
      Alignment,
      Autoformat,
      AutoImage,
      AutoLink,
      Autosave,
      BalloonToolbar,
      Base64UploadAdapter,
      BlockQuote,
      Bold,
      Code,
      CodeBlock,
      Essentials,
      FindAndReplace,
      FontBackgroundColor,
      FontColor,
      FontFamily,
      FontSize,
      GeneralHtmlSupport,
      Heading,
      Highlight,
      HorizontalLine,
      HtmlComment,
      HtmlEmbed,
      ImageBlock,
      ImageCaption,
      ImageInline,
      ImageInsert,
      ImageInsertViaUrl,
      ImageResize,
      ImageStyle,
      ImageTextAlternative,
      ImageToolbar,
      ImageUpload,
      Indent,
      IndentBlock,
      Italic,
      Link,
      LinkImage,
      List,
      ListProperties,
      Markdown,
      MediaEmbed,
      Mention,
      PageBreak,
      Paragraph,
      PasteFromMarkdownExperimental,
      PasteFromOffice,
      RemoveFormat,
      SelectAll,
      ShowBlocks,
      SpecialCharacters,
      SpecialCharactersArrows,
      SpecialCharactersCurrency,
      SpecialCharactersEssentials,
      SpecialCharactersLatin,
      SpecialCharactersMathematical,
      SpecialCharactersText,
      Strikethrough,
      Style,
      Subscript,
      Superscript,
      Table,
      TableCaption,
      TableCellProperties,
      TableColumnResize,
      TableProperties,
      TableToolbar,
      TextPartLanguage,
      TextTransformation,
      TodoList,
      Underline,
      Undo],
    toolbar: {
      items: ['undo',
        'redo',
        '|',
        'showBlocks',
        'findAndReplace',
        'textPartLanguage',
        '|',
        'heading',
        'style',
        '|',
        'fontSize',
        'fontFamily',
        'fontColor',
        'fontBackgroundColor',
        '|',
        'bold',
        'italic',
        'underline',
        'strikethrough',
        'subscript',
        'superscript',
        'code',
        'removeFormat',
        '|',
        'specialCharacters',
        'horizontalLine',
        'pageBreak',
        'link',
        'insertImage',
        'insertImageViaUrl',
        'mediaEmbed',
        'insertTable',
        'highlight',
        'blockQuote',
        'codeBlock',
        'htmlEmbed',
        '|',
        'alignment',
        '|',
        'bulletedList',
        'numberedList',
        'todoList',
        'outdent',
        'indent']
    },
    shouldNotGroupWhenFull: false,
    language: 'zh-cn',
    link: {
      addTargetToExternalLinks: true,
      defaultProtocol: 'https://',
      decorators: {
        toggleDownloadable: {
          mode: 'manual',
          label: 'Downloadable',
          attributes: {
            download: 'file'
          }
        }
      }
    },
    list: {
      properties: {
        styles: true,
        startIndex: true,
        reversed: true
      }
    },
    mention: {
      feeds: [
        {
          marker: '@',
          feed: [
            /* See: https://ckeditor.com/docs/ckeditor5/latest/features/mentions.html */
          ]
        }
      ]
    },
    menuBar: {
      isVisible: true
    },
    placeholder: '在此处键入或粘贴您的内容！',
    style: {
      definitions: [
        {
          name: 'Article category',
          element: 'h3',
          classes: ['category']
        },
        {
          name: 'Title',
          element: 'h2',
          classes: ['document-title']
        },
        {
          name: 'Subtitle',
          element: 'h3',
          classes: ['document-subtitle']
        },
        {
          name: 'Info box',
          element: 'p',
          classes: ['info-box']
        },
        {
          name: 'Side quote',
          element: 'blockquote',
          classes: ['side-quote']
        },
        {
          name: 'Marker',
          element: 'span',
          classes: ['marker']
        },
        {
          name: 'Spoiler',
          element: 'span',
          classes: ['spoiler']
        },
        {
          name: 'Code (dark)',
          element: 'pre',
          classes: ['fancy-code', 'fancy-code-dark']
        },
        {
          name: 'Code (bright)',
          element: 'pre',
          classes: ['fancy-code', 'fancy-code-bright']
        }
      ]
    },
    table: {
      contentToolbar: ['tableColumn', 'tableRow', 'mergeTableCells', 'tableProperties', 'tableCellProperties']
    },
  }
});

defineExpose({
  getData() {
    return getFormData();
  },
});

const getFormData = () => {
  return state.rulesForm
}

const searchStyle = (style = 0) => {
  switch (style) {
    case 0:
      state.searchDow = false;
      state.searchUp = true;
      state.cardClass = "card-search-dow";
      state.searchButton = "search-item-button-dow";
      break;
    default:
      state.searchDow = true;
      state.searchUp = false;
      state.cardClass = "card-search";
      state.searchButton = "search-item-button";
      break;
  }
};

const emits = defineEmits<{ (e: "click", data: object): void }>();

const props = defineProps({
  rulesForm: {
    type: Array,
    default: () => [],
  },
  rules: {
    default: {},
  },
  type: null as any,
  refresh: null as any,
  labelWidth: {
    type: String,
    default: "120px",
  },
  buttons: []
});

onMounted(() => {
  state.labelWidth = <string>props.labelWidth
  state.buttons = props.buttons
  loadForm()
})

const handleSuccess = (
    response: any, uploadFile: UploadFile, uploadFiles: UploadFiles, name: string
) => {
  if (!state.rulesForm[name]) {
    state.rulesForm[name] = []
  }
  state.rulesForm[name].push(response?.data?.linkUrl)
  imageUrl.value = response?.data?.linkUrl
  console.log('handleSuccess', state.rulesForm[name])
  if (response.code == 0) {
    emits("click", {type: name, data: response?.data?.linkUrl});
  } else {
    uploadFile['name'] = ''
  }
};

/**
 * 删除文件
 */
const handleRemove = (
    uploadFile: any,
    uploadFiles: any,
    dataForm: any
) => {
};

/** 图片预览 **/
const handlePictureCardPreview: UploadProps["onPreview"] = (uploadFile) => {
  dialogImageUrl.value = uploadFile.url!;
  dialogVisible.value = true;
};

/**
 * 上传文件之前
 * @param rawFile 文件
 * @param suffix 后缀
 * @param size 文件大小
 */
const beforeUploadFile = (
    rawFile: any,
    suffix = ["image/jpeg"],
    size = 1024 * 1024 * 5,
    key: any
) => {
  if (!suffix.includes(rawFile.type)) {
    ElMessage.error("只能上传如下格式文件、" + suffix.toString()
        .replaceAll('application/', '.')
        .replaceAll('image/', '.'));
    rawFile.name = ''
    return false;
  } else if (rawFile.size >= size) {
    ElMessage.error("只能上传小于" + size / 1024 / 1024 + "MB大文件!");
    return false;
  }
  state.rulesForm[key + 'File'] = rawFile
  // state.rulesForm[key] = rawFile.name
  return true;
};

watch(
    () => props.refresh,
    (refresh) => {
      loadForm();
    }
);

const loadForm = () => {
  state.type = props.type || "form";
  if (!props.rulesForm) {
    return;
  }
  const rulesForm = {} as any;
  let dataForm = [] as any, rules = {} as any;
  props.rulesForm.forEach((e: any) => {
    let form = {card: [], label: e.label || undefined};
    let cards: any[] = [];
    e.card.forEach(
        (card: { [x: string]: any; line: any; name: string | number }) => {
          if (card.rules) {
            rules[card.key] = card.rules;
          }
          if (card.line) {
            let cardLine = Object.assign([], cards);
            form.card.push({line: cardLine});
            cards = Object.assign([], []);
          } else {
            rulesForm[card.key] = card[card.key] ? card[card.key] : state.rulesForm[card.key];
            if (state.type === "see") {
              card.disabled = "disabled";
            }
            cards.push(card);
          }
        }
    );
    let cardLine = Object.assign([], cards);
    form.card.push({line: cardLine});
    dataForm.push(form);
  });
  state.dataForm = dataForm as any;
  state.rulesForm = rulesForm as any;
  if (Object.keys(props.rules).length > 0) {
    rules = props.rules
  }
  state.rules = rules || {};
  restForm(false)
};

/**
 * 提交数据【并验证数据完事性】
 */
const handleSubmit = (type: string) => {
  if (!ruleFormRef.value) {
    return;
  }
  ruleFormRef.value?.validate().then(valid => {
    handleCallback(type)
  }).catch((fields: any) => {
    const keysArray = Object.keys(fields);
    if (keysArray && keysArray.length > 0) {
      const firstKey = keysArray[0] || ''
      const field = fields[firstKey][0] || {}
      ElMessage.error(field?.message || "验证未通过");
    } else {
      ElMessage.error("验证未通过");
    }
  })
};

const handleCallback = (type: string) => {
  if (state.type === "search" && !state.searchDow) {
    state.searchDow = true;
    state.searchUp = false;
    state.cardClass = "card-search-dow";
    state.searchButton = "search-item-button-dow";
  }
  let typeList = ["select", "cascader", "selecttime"];
  const forms = [] as any;
  props.rulesForm.forEach((form: any) => {
    const dataList = form.card.filter(({type = ""}) =>
        typeList.includes(type)
    );
    dataList?.forEach((data: any) => {
      forms.push(data);
    });
  });
  forms.forEach((e: any) => {
    try {
      let value = state.rulesForm[e.key];
      let options = e.options;
      let label;
      if (e.type === "cascader") {
        label = [];
        const valueKey = e.props && e.props.value ? e.props.value : 'value'
        const labelKey = e.props && e.props.label ? e.props.label : 'label'
        value?.forEach((v: any) => {
          if (v) {
            let opt = options.find((f: any) => f[valueKey] === v);
            label.push(opt[labelKey]);
            options = opt.children;
          }
        });
      } else {
        let opt = options.find((f: any) => f.value === value);
        label = opt?.label;
      }
      state.rulesForm[e.key + "Name"] = label;
    } catch (e: any) {
      console.error("handleCallback", e);
    }
  });
  try {
    emits("click", {type: (typeof type === "string") ? type : "submit", data: state.rulesForm, ref: ruleFormRef});
  } catch (e: any) {
    console.error("emits", e);
  }
};

/**
 * 重置表单
 */
const restForm = (rest = true) => {
  if (rest) {
    emits("click", {type: "rest"});
  }
  if (!ruleFormRef.value) {
    return;
  }
  ruleFormRef.value?.resetFields();
};
/**
 * 关闭表单
 */
const handleClose = () => {
  emits("click", {type: "formClose"});
};

const onChange = (value: any, name: any) => {
  emits("click", {type: name, data: value});
};

const formatterValue = (cellValue: any, dataValue: any) => {
  if (!dataValue) {
    return cellValue;
  }
  try {
    if (typeof dataValue === "string") {
      if (cellValue) {
        return moment(new Date(cellValue)).format(dataValue);
      } else {
        return "";
      }
    }
    let item = dataValue.find((item: any) => item.value === (cellValue || 0));
    if (item) {
      cellValue = item.label;
    } else {
      cellValue = "";
    }
  } catch (e: any) {
    console.error(e);
    cellValue = "";
  }
  return cellValue || "";
}

const onReady = (editor: any) => {
  editor.plugins.get("FileRepository").createUploadAdapter = (loader: any) => {
    console.log('FileRepository', loader)
    return new UploadAdapter(loader);
  };
  editor.ui.getEditableElement().parentElement.insertBefore(
      editor.ui.view.toolbar.element,
      editor.ui.getEditableElement()
  );
}

class UploadAdapter {
  constructor(loader: any) {
    loaders = loader
  }

  upload = () => {
    // 因为编辑器插件需要返回一个promise对象
    return new Promise((resolve, reject) => {
      // 读取选中的文件
      loaders.file.then((file: any) => {
        // 我们通过formData进行图片上传
        let data = new FormData();
        data.append('file', file);
        uploadFilesApi(data)
            .then((result: any) => {
              if (result.code === 0) {
                // 定义插件返回格式
                resolve({
                  default: result.data.linkUrl
                });
              } else {
                reject(result.message);
              }
            })
      })
    })
  }
  abort = () => {
    // 图片删除方法，可以调用后端api进行文件删除
  }
}

const defaultFileList = (files: string[]) => {
  return files
}


</script>
