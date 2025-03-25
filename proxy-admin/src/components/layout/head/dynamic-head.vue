<template>
  <div class="dynamic-head">
    <div class="head-info">
      <div class="tabs-collapse">
        <el-icon :size="30" @click="isCollapse" v-if="!state.isCollapse">
          <fold/>
        </el-icon>
        <el-icon :size="30" @click="isCollapse" v-else>
          <expand/>
        </el-icon>
      </div>
      <div class="head-info-title">CdkjFw 维基框架单点登录平台 - 管理中心</div>
    </div>
    <div class="head-right">
      <span class="head-time">{{ state.dateTime }}</span>
      <span class="head-week">{{ state.week }}</span>
      <span class="head-avatar">
        <el-avatar v-if="state.user.avatar" :size="50" :src="state.user.avatar || state.avatar"/>
        <el-avatar v-else :icon="iconUser"/>
      </span>
      <span class="head-user-type">
        <el-dropdown @command="handleCommand">
        <span class="el-dropdown-link">
          {{ state.user.userTypeName }}<el-icon class="el-icon--right"><arrow-down/></el-icon>
        </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="0">修改密码</el-dropdown-item>
              <el-dropdown-item command="1">退出系统</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </span>
    </div>
  </div>
</template>

<script setup lang="ts">
import {ArrowDown, Expand, Fold, UserFilled} from '@element-plus/icons-vue'
import {onMounted, reactive} from 'vue';
import {formatToDateTimeCn, formatToWeek} from '@/utils/dates';
import Storage from '@/utils/storage';
import {USER_INFO_KEY} from '@/utils/enums/cache-enum';
import {ElMessage, ElMessageBox} from 'element-plus';
import {logoutApi} from '@/api/user';
import {useRouter} from 'vue-router';
import type {RmsUser} from "@/model/rms/RmsUser";

const router = useRouter();
const iconUser = UserFilled
const arrowDown = ArrowDown
const fold = Fold
const expand = Expand

const emits = defineEmits<{ (e: 'click', data: object): void }>();

const state = reactive({
  dateTime: formatToDateTimeCn(),
  week: formatToWeek(),
  user: Storage.get(USER_INFO_KEY) as RmsUser,
  avatar: '/assets/avatar.png',
  isCollapse: false
})

onMounted(() => {
  window.setInterval(() => {
    state.dateTime = formatToDateTimeCn()
    state.week = formatToWeek()
  }, 1000)
})

const isCollapse = () => {
  state.isCollapse = !state.isCollapse
  emits('click', {type: 'isCollapse', data: state.isCollapse})
}

const handleCommand = (command: number) => {
  if (parseInt(command.toString()) === 1) {
    ElMessageBox.confirm(
        '是否确认退出系统?',
        '提示',
        {
          confirmButtonText: '确认',
          cancelButtonText: '取消',
          type: 'warning',
        }
    )
        .then(() => {
          logoutApi()
            .then(() => {
              Storage.clear()
              ElMessage({
                type: 'success',
                message: '退出成功！',
                onClose: function () {
                  location.href = '/'
                }
              })
            })
        })
        .catch(() => {
          ElMessage({
            type: 'info',
            message: '取消退出',
          })
        })
  }
}
</script>
