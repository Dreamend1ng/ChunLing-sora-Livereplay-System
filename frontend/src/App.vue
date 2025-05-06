<template>
  <!--<MainPage/>
  <LogIn />-->
    <div>
      <router-view></router-view>
    </div>
</template>

<script>
//import HelloWorld from './components/HelloWorld.vue'
//import MainPage from './components/MainPage.vue'
//import LogIn from './components/LogIn.vue'
export default {
  name: 'App',
  components: {
    //MainPage,
    //LogIn
  }
}
</script>

<style>
#app {
  max-width: none;
  font-family: Avenir, Helvetica, Arial, sans-serif;
  /*-webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;*/
  text-align: center;
}
</style>
