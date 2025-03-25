<template>
  <div class="dynamic-table">
    <div class="dynamic-table-search" v-if="state.searchForm">
      <DynamicForm ref="dynamicFormRef"
                   type="search"
                   :rulesForm="state.searchForm"
                   @click="callBackSearch"/>
    </div>
    <!--表格顶部工具 start-->
    <template v-if="state.table.header">
      <div class="dynamic-table-header">
        <div class="header-title">
          <slot name="header"/>
          <el-button v-for="btn in state.table.button" @click="customControl('', btn.method)" :key="btn.method"
                     :type="btn.type">{{ btn.text }}
          </el-button>
        </div>
        <div class="header-function" v-if="state.table.add">
          <el-button link="true" type="primary" @click="()=>{rowHandle('add')}">添加</el-button>
          <el-button link="true" type="primary" @click="()=>{rowHandle('delete')}">删除</el-button>
        </div>
      </div>
    </template>
    <el-table
        :data="state.data"
        style="width: 100%"
        class="table"
        ref="tableRef"
        @select="select"
        @select-all="selectionChange"
        @selection-change="selectionChange"
        @row-click="rowClick"
        @row-dblclick="rowDblClick"
        :highlight-current-row="state.table.add"
        @current-change="handleCurrentChange"
        header-row-class-name="header-row-class"
        header-cell-class-name="header-cell-class"
        row-class-name="row-class"
    >
      <template v-for="(item, index) in state.columns">
        <el-table-column
            v-if="item.type === 'checkbox'"
            type="selection"
            width="50"
            :key="item.key"
        />
        <el-table-column
            v-else-if="item.type === 'index'"
            :key="item.key"
            type="index"
            align="center"
            width="65"
            :label="item.label || '序号'"
        >
          <template #default="scope">{{ scope.$index + 1 }}</template>
        </el-table-column>
        <el-table-column
            v-else-if="item.type === 'number'"
            :key="item.key"
            show-overflow-tooltip
            type="index"
            align="center"
            width="65"
            :label="item.label || '序号'"
        >
          <template #default="scope"
          >{{
              scope.$index + 1 + ((state.table.pageIndex ? state.table.pageIndex : 1) - 1) * state.table.pageSize
            }}
          </template>
        </el-table-column>
        <!--自定义插槽-->
        <el-table-column
            v-else-if="item.type === 'slot'"
            :width="item.width"
            :align="item.align"
            :key="item.key"
            :label="item.label">
          <template #default="scope">
            <slot
                :name="item.slot"
                v-bind="{ scope }"
            ></slot>
          </template>
        </el-table-column>
        <el-table-column
            v-else-if="item.type === 'text'"
            :align="item.align"
            :label="item.label"
            :key="item.key"
            :width="item.width || 100"
        >
          <template #default="scope">
            <el-input
                v-model="scope.row[item.key]"
                :placeholder="item.placeholder || '输入内容'"
                :readOnly="item.readOnly || false"
                :style="item.style || { width: 200 }"
                @change="customControl(scope.row, item.key)"
            />
          </template>
        </el-table-column>
        <el-table-column
            v-else-if="item.type === 'textarea'"
            :align="item.align"
            :label="item.label"
            :key="item.key"
            :width="item.width || 100"
        >
          <template #default="scope">
            <el-input
                v-model="scope.row[item.key]"
                :placeholder="item.placeholder || '输入内容'"
                :readOnly="item.readOnly || false"
                :style="item.style || { width: 200 }"
                @change="customControl(scope.row, item.key)"
            />
          </template>
        </el-table-column>
        <el-table-column
            v-else-if="item.type === 'password'"
            :align="item.align"
            :label="item.label"
            :key="item.key"
            :width="item.width || 100"
        >
          <template #default="scope">
            <el-input
                v-model="scope.row[item.key]"
                :type="item.type"
                :placeholder="item.placeholder || '输入内容'"
                :readOnly="item.readOnly || false"
                :style="item.style || { width: 200 }"
                @change="customControl(scope.row, item.key)"
            />
          </template>
        </el-table-column>
        <el-table-column
            v-else-if="item.type === 'number'"
            :align="item.align"
            :label="item.label"
            :key="item.key"
            :width="item.width || 100"
        >
          <template #default="scope">
            <el-input
                v-model="scope.row[item.key]"
                :type="item.type"
                :placeholder="item.placeholder || '输入内容'"
                :readOnly="item.readOnly || false"
                :style="item.style || { width: 200 }"
                @change="customControl(scope.row, item.key)"
            />
          </template>
        </el-table-column>
        <el-table-column
            v-else-if="item.type === 'tags'"
            :align="item.align"
            :label="item.label"
            :key="item.key"
            :width="item.width || 100"
        >
          <template #default="scope">
            <el-tag
                v-for="(tag, idx) in item.tags"
                :type="tag.type || 'success'"
                :key="'tag-' + index + '-' + idx"
            >
              {{ scope.row[item.key] }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column
            v-else-if="item.type === 'select'"
            :align="item.align"
            :label="item.label"
            :key="item.key"
            :width="item.width || 100"
        >
          <template #default="scope">
            <el-select
                v-model="scope.row[item.key]"
                :placeholder="item.placeholder || '请选择'"
                :readOnly="item.readOnly || false"
                :style="item.style || { width: 200 }"
                @change="customControl(scope.row, item.key)"
            >
              <el-option
                  v-for="(opt, idx) in item.options"
                  :key="'select-' + index + '-' + idx"
                  :label="opt.label"
                  :value="opt.value"
              />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column
            v-else-if="item.type === 'search'"
            :align="item.align"
            :label="item.label"
            :key="item.key"
            :width="item.width || 100"
        >
          <template #default="scope">
            <el-input
                v-model="scope.row[item.key]"
                :type="item.type"
                :placeholder="item.placeholder || '输入内容'"
                :readOnly="item.readOnly || false"
                :style="item.style || { width: 200 }"
                @change="customControl(scope.row, item.key)"
            >
              <template #append>
                <el-button
                    :icon="Search"
                    @click="customControl(scope.row, item.key)"
                />
              </template>
            </el-input>
          </template>
        </el-table-column>
        <el-table-column
            v-else-if="item.type === 'switch'"
            :align="item.align"
            :label="item.label"
            :key="item.key"
            :width="item.width || 100"
        >
          <template #default="scope">
            <el-switch
                v-model="scope.row[item.key]"
                :active-text="item.active"
                :active-value="item.value"
                :inactive-text="item.inactive"
                :inactive-value="item.invalue"
                @click="customControl(scope.row, item.key)"
            />
          </template>
        </el-table-column>
        <el-table-column
            v-else-if="item.type === 'datetime'"
            :align="item.align"
            :label="item.label"
            :key="item.key"
            :width="item.width || 100"
        >
          <template #default="scope">
            <el-date-picker
                v-model="scope.row[item.key]"
                type="datetime"
                :placeholder="item.placeholder || '输入内容'"
                @change="customControl(scope.row, item.key)"
            />
          </template>
        </el-table-column>
        <el-table-column
            v-else-if="item.type === 'datePicker'"
            :align="item.align"
            :label="item.label"
            :key="item.key"
            :width="item.width || 100"
        >
          <template #default="scope">
            <el-date-picker
                v-model="scope.row[item.key]"
                type="datePicker"
                :placeholder="item.placeholder || '输入内容'"
                @change="customControl(scope.row, item.key)"
            />
          </template>
        </el-table-column>
        <el-table-column
            v-else-if="item.type === 'date'"
            :align="item.align"
            :label="item.label"
            :key="item.key"
            :width="item.width || 100"
        >
          <template #default="scope">
            <el-date-picker
                v-model="scope.row[item.key]"
                type="date"
                :placeholder="item.placeholder || '输入内容'"
            />
          </template>
        </el-table-column>
        <el-table-column
            v-else-if="item.type === 'rangePicker'"
            :align="item.align"
            :label="item.label"
            :key="item.key"
            :width="item.width || 100"
        >
          <template #default="scope">
            <el-date-picker
                v-model="scope.row[item.key]"
                type="rangePicker"
                :placeholder="item.placeholder || '输入内容'"
                @change="customControl(scope.row, item.key)"
            />
          </template>
        </el-table-column>
        <el-table-column
            v-else-if="item.type === 'cascader'"
            :align="item.align"
            :label="item.label"
            :key="item.key"
            :width="item.width || 100"
        >
          <template #default="scope">
            <el-cascader
                :options="item.options"
                v-model="scope.row[item.key]"
                type="rangePicker"
                :placeholder="item.placeholder || '输入内容'"
                @change="customControl(scope.row, item.key)"
            />
          </template>
        </el-table-column>
        <el-table-column
            v-else-if="item.type === 'image'"
            :key="item.key"
            :align="item.align"
            :label="item.label"
            :width="item.width || 100"
        >
          <template #default="scope">
            <el-link :href="scope.row[item.key]" target="_blank">
              <img
                  alt="查看"
                  :src="scope.row[item.key]"
                  :data-image="scope.row[item.key]"
                  data-uid="showQrCode"
                  width="25px"
              />
            </el-link>
          </template>
        </el-table-column>
        <el-table-column
            v-else-if="item.type === 'cover'"
            :key="item.key"
            :align="item.align"
            :label="item.label"
            :width="item.width || 100"
        >
          <template #default="scope">
            <el-image
                alt="查看"
                style="width: 50px; height: 50px"
                :src="scope.row[item.key]"
                :zoom-rate="1.2"
                :preview-src-list="[scope.row[item.key]]"
                :initial-index="4"
                fit="cover"/>
          </template>
        </el-table-column>
        <el-table-column
            v-else-if="item.type === 'format'"
            :prop="item.key"
            :align="item.align"
            :key="item.key"
            :label="item.label"
            :width="item.width || 100"
            :formatter="formatterValue"/>
        <el-table-column
            v-else-if="item.type === 'link'"
            :prop="item.key"
            :align="item.align"
            :key="item.key"
            :label="item.label"
            :width="item.width || 100">
          <template #default="scope">
            <el-link type="primary" :href="scope.row[item.key]" target="_blank">查看</el-link>
          </template>
        </el-table-column>
        <el-table-column
            v-else-if="item.type === 'formatDateTime'"
            :prop="item.key"
            :key="item.key"
            :align="item.align"
            :label="item.label"
            :width="item.width || 100"
            :formatter="formatterDateTime"
            :column-key="item.formatter"
        />
        <el-table-column
            v-else-if="item.type === 'custom'"
            :align="item.align"
            :key="item.key"
            :label="item.label || '操作'"
            :fixed="item.fixed"
            :width="item.width"
        >
          <template #default="scope">
            <el-button
                v-for="(button, idx) in item.buttons"
                :key="'button-' + index + '-' + idx"
                @click="
                (e) => {
                  e.stopPropagation();
                  customControl(scope.row, button.method);
                }
              "
                :type="button.type || 'primary'"
                :link="!button.link"
                :icon="button.icon"
                :style="button.style"
                :value="scope.row['id']"
            >{{ button.text }}
            </el-button>
          </template>
        </el-table-column>
        <el-table-column
            v-else
            :prop="item.key"
            :align="item.align"
            :label="item.label"
            :key="item.key"
            :width="item.width || 100"
        >
          <template #default="scope">
            <div
                class="row-class-col-text"
                :title="scope.row[item.key]"
                v-html="scope.row[item.key]"
            />
          </template>
        </el-table-column>
      </template>
      <el-table-column v-if="state.table.repair || true"/>
    </el-table>
    <div class="page-footer">
      <el-pagination
          class="page-class"
          v-if="state.table.page"
          v-model:current-page="currentPage4"
          v-model:page-size="state.table.pageSize"
          :page-sizes="state.table.pageSizes"
          :size="state.table.pageSize"
          layout="total, sizes, prev, pager, next"
          :total="state.table.total"
          @size-change="currentPageSizes"
          @current-change="currentPageSize"
      />
      <!--      <el-pagination-->
      <!--          class="page-class"-->
      <!--          v-if="state.table.page"-->
      <!--          v-model:current-page="state.table.pageIndex"-->
      <!--          v-model:page-size="state.table.pageSize"-->
      <!--          :page-sizes="state.table.pageSizes"-->
      <!--          :size="state.table.pageSize"-->
      <!--          layout="total, sizes, prev, pager, next, jumper"-->
      <!--          :total="state.table.total"-->
      <!--          @size-change="currentPageSizes"-->
      <!--          @current-change="currentPageSize"-->
      <!--      />-->
    </div>
  </div>
</template>

<script setup lang="ts">
import {onMounted, reactive, ref, watch} from "vue";
import moment from "moment";
import {DATETIME_RANGE, FORMAT, FORMAT_DATETIME, SELECT, TEXT} from "@/utils/enums/type-enums";

import DynamicForm from '@/components/layout/form/dynamic-form.vue'
import type {Buttons} from "@/components/layout/common/common";
import type {TableColumns} from "@/components/layout/table/table-columns";
import type {Card} from "@/components/layout/form/form";

const dynamicFormRef = ref(DynamicForm)
const tableRef = ref()
const currentRow = ref()

const state = reactive({
  columns: [] as TableColumns[],
  data: [] as any,
  searchForm: null as any,
  table: {
    repair: true as boolean,
    pageIndex: 1 as number,
    pageSize: 15 as number,
    page: true as boolean,
    pageSizes: [15, 30, 60, 100] as any,
    total: 0 as number,
    header: false as boolean,
    button: [] as Buttons[],
    add: false as boolean
  },
});

defineExpose({
  getData() {
    return state.data;
  },
  getCurrentData() {
    return currentRow.value;
  },
});

const props = defineProps({
  columns: {
    type: Array,
    default: () => [] as TableColumns[]
  },
  data: {
    type: Array,
    default: () => []
  },
  table: {
    default: {
      repair: true,
      pageIndex: 1,
      pageSize: 15,
      page: true,
      pageSizes: ["15", "30", "60", "100"],
      total: 0,
      header: false,
      button: [] as Buttons[],
      add: false
    }
  },
});

const emits = defineEmits<{ (e: "click", data: object): void }>();

onMounted(() => {
  init()
});

const init = () => {
  state.columns = props.columns as TableColumns[];
  state.data = props.data || [] as any;
  state.table = Object.assign(state.table, props.table);
  state.table.pageIndex = state.table.pageIndex < 1 ? 1 : state.table.pageIndex
  if (!state.columns) {
    return false
  }
  // 查询全部查询字段
  const columns = state.columns.filter((f: any) => f.search)
  if (columns && columns.length > 0) {
    const search = [] as any
    columns.forEach((tc: TableColumns) => {
      let form: Card = tc.form as Card
      let value = {
        placeholder: '输入' + form.label, span: 5
      } as any
      Object.assign(value, form)
      const type = form.type || ''
      switch (type) {
        case FORMAT_DATETIME:
          value.type = DATETIME_RANGE
          value.span = 8
          break
        case FORMAT:
          value.type = SELECT
          break
        case '':
          value.type = TEXT
          break
        default:
          value.type = form.type
          break
      }
      value.width = value.labelWidth || 120
      search.push(value)
    })
    search.sort((a: any, b: any) => (a.sort || 0) - (b.sort || 0));
    state.searchForm = [{label: null, card: search}]
  }
}

watch(
    () => [
      props.data,
      props.table.total,
      props.table.pageIndex,
      props.table.page,
      props.table.repair,
      props.table.pageSize,
      props.columns
    ],
    (newValue, oldValue) => {
      if (newValue[0]) {
        state.data = newValue[0];
      }
      if (newValue[1]) {
        state.table.total = (newValue[1] || 0) as number;
      }
      if (newValue[2]) {
        let value: number = newValue[2] as number
        state.table.pageIndex = value < 1 ? 1 : value;
      }
      if (newValue[3] != null) {
        state.table.page = newValue[3] as boolean;
      }
      if (newValue[4] != null) {
        state.table.repair = newValue[4] as boolean;
      }
      if (newValue[5] != null) {
        state.table.pageSize = newValue[5] as number;
      }
      if (newValue[6]) {
        init()
      }
    }
);

const currentPageSizes = (value: number) => {
  emits("click", {type: "pageSize", data: value});
};
const currentPageSize = (value: number) => {
  emits("click", {type: "page", data: value});
};

const callBackSearch = (value: any) => {
  const {type = '', data = null} = value
  emits("click", {type: type, data: data});
}

// 自定义控制
const customControl = (row: any, type: string) => {
  emits("click", {type: type, data: row});
};
const select = (selection: any, row: any) => {
  emits("click", {type: "select", data: row, value: selection});
};
const selectionChange = (selection: any) => {
  emits("click", {type: "selectAll", data: selection});
};
const rowClick = (row: any, column: any, event: any) => {
  event.stopPropagation();
  emits("click", {type: "click", data: row, column: column});
};
const rowDblClick = (row: any, column: any, event: any) => {
  event.stopPropagation();
  emits("click", {type: "dblclick", data: row, column: column});
};
/**
 * 指定值转换
 * @param row 行数据
 * @param column 列名
 * @param cellValue 值
 * @param index 索引
 */
const formatterValue = (row: any, column: any, cellValue: any, index: any) => {
  try {
    let col: TableColumns = state.columns.find((c: TableColumns) => c.key === column.property);
    let dataValue = col.value;
    if (typeof dataValue === "string") {
      dataValue = JSON.parse(col.value);
    }
    if (!dataValue) {
      return ''
    }
    let value: number | null = null;
    if (cellValue == null || cellValue == undefined || (typeof cellValue === 'number' && isNaN(cellValue))) {
      value = -100
    } else {
      value = cellValue
    }
    let item = dataValue.find((item: any) => item.value === value);
    if (item) {
      cellValue = item.label;
    } else {
      cellValue = ''
    }
  } catch (e) {
    console.log(e);
  }
  return cellValue
};
/**
 * 格式日期
 * @param row
 * @param column
 * @returns {string}
 */
const formatterDateTime = (row: any, column: any) => {
  let time = row[column.property];
  if (!time) {
    return "";
  }
  time = time.replace('T', ' ')
  if (/^[0-9]*$/.test(time)) {
    if (time.length === 10) {
      time += '000'
    }
    return moment(parseInt(time)).format(column.columnKey);
  } else {
    return moment(time).format(column.columnKey);
  }
};

const rowHandle = (type: any) => {
  switch (type) {
    case 'add':
      state.data.push({_id: Math.random()})
      break
    default:
      if (currentRow.value) {
        const data = state.data.find(({_id = ''}) => _id === currentRow.value._id)
        const index = state.data.indexOf(data)
        if (index > -1) {
          state.data.splice(index, 1)
        }
      }
      break
  }
}
const handleCurrentChange = (value: any) => {
  console.log('handleCurrentChange', value)
  currentRow.value = value
}
</script>
