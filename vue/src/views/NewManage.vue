<template>
  <el-container>
    <el-aside :width="data.asideWidth" style="min-height: 100vh;background-color: #001529">
      <div
          style="height: 60px; line-height: 60px; font-size: 20px; display: flex; align-items: center; justify-content: center">
        <img alt="" src="../assets/imgs/logo.png" style="width: 30px;">
        <span v-show="!data.isCollapse" class="logo-title" style="color: white;">管理系统</span>
      </div>
      <el-menu
          router
          :collapse="data.isCollapse"
          :collapse-transition="false"
          :default-active="$route.path"
          :default-openeds="['/home']"
          background-color="#001529"
          class="el-menu-vertical-demo"
          style="border: none"
          text-color="rgba(255, 255, 255, 0.65)"
      >
        <el-menu-item index="/home">
          <el-tooltip content="系统首页" placement="right">
            <el-icon>
              <HomeFilled/>
            </el-icon>
          </el-tooltip>
          <span>系统首页</span>
        </el-menu-item>
        <el-sub-menu index="2">
          <template #title>
            <el-icon>
              <Memo/>
            </el-icon>
            <span>各项功能</span>
          </template>

          <el-menu-item index="/recognize">
            <el-icon>
              <Document/>
            </el-icon>
            <span>出入库登记</span>
          </el-menu-item>
         <el-menu-item index="/pay">
            <el-icon>
              <Document/>
            </el-icon>
            <span>车费支付</span>
          </el-menu-item>

        </el-sub-menu>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header>
        <el-button :icon="data.CollapseIcon" round="true" size="medium" text @click="handleCollapse"></el-button>


        <div style="flex: 1; width: 0; display: flex; align-items: center; justify-content: flex-end">
          <el-button :icon="FullScreen" text  @click="handleFull"></el-button>
          <el-dropdown placement="bottom">
            <div style="display: flex; align-items: center; cursor: default">
            <img src="@/assets/imgs/logo.png"  style="width: 40px; height: 40px; margin: 0 5px">
              <span id="username" style="margin-left: 5px">管理员</span>
            <el-icon class="el-icon--right">
              <arrow-down/>
            </el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item>个人信息</el-dropdown-item>
                <el-dropdown-item>修改密码</el-dropdown-item>
                <el-dropdown-item @click="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main>
        <div style="flex: 1; width: 100%; background-color: #f8f8ff; padding: 10px">
          <router-view />
        </div>
      </el-main>
    </el-container>
  </el-container>

</template>

<script setup>
import {useRoute} from 'vue-router'
import {reactive, ref} from "vue";
import {ArrowRight, FullScreen, Menu} from "@element-plus/icons-vue";


const data = reactive({
  asideWidth: '250px',
  isCollapse: false,
  textCollapse: '收起',
  CollapseIcon: 'Fold'
})
const handleCollapse = () => {
  data.isCollapse = !data.isCollapse
  data.asideWidth = data.isCollapse ? '64px' : '250px'
  data.textCollapse = data.isCollapse ? '展开' : '收起'
  data.CollapseIcon = data.isCollapse ? 'Expand' : 'Fold'
}

const  handleFull = () => {
  document.documentElement.requestFullscreen()
}
</script>


<style scoped>

.el-menu--inline {
  background-color: #000c17 !important;
}

.el-menu--inline .el-menu-item {
  background-color: #000c17 !important;
  padding-left: 49px !important;
}

.el-menu-item:hover, .el-sub-menu__title:hover {
  color: #fff !important;
}

.el-submenu__title:hover i {
  color: #fff !important;
}

.el-menu-item:hover i {
  color: #fff !important;
}

.el-menu-item.is-active {
  background-color: #1890ff !important;
  border-radius: 10px !important;
  width: calc(100% - 8px);
  margin-left: 4px !important;
}

.el-menu-item.is-active {
  color: #f3f3f3;
}

.el-menu-item {

  height: 50px !important;
  line-height: 50px !important;
}

.el-submenu__title {

  height: 50px !important;
  line-height: 50px !important;
}

.el-menu-item.is-active i, .el-menu-item.is-active .el-tooltip {
  margin-left: -4px;
}

.el-submenu .el-menu-item {

  min-width: 0 !important;
}

.el-menu--inline .el-menu-item.is-active {
  padding-left: 49px !important;
}

.el-aside {
  transition: width .3s;
  box-shadow: 2px 0 6px rgba(0, 21, 41, .35);
}

.logo-title {
  margin-left: 5px;
  font-size: 20px;
  transition: all .3s;
}

.el-header {
  box-shadow: 2px 0 6px rgba(0, 21, 41, .35);
  display: flex;
  align-items: center;
}

.el-menu-vertical-demo:not(.el-menu--collapse) {
  width: 250px;
  min-height: 400px;
}
</style>