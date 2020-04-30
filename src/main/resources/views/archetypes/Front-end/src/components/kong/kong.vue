<template>
  <div>
      11
  </div>
</template>
  <script>
export default {
  data() {
    return {};
  },
  created() {
    debugger;
    const url = this.$route.query.url;
    this.$router.push({
      path: url
    });
  }
};
</script>
  <style>
</style>