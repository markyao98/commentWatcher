// 获取侧边栏元素
const sidebar = document.getElementById('sidebar');

// 获取侧边栏中的所有链接
const links = sidebar.getElementsByTagName('a');

// 给每个链接添加点击事件
for (let i = 0; i < links.length; i++) {
    links[i].addEventListener('click', function() {
        // 获取链接的href属性
        const href = this.getAttribute('href');

        // 跳转到对应的页面
        window.location.href = href;
    });
}
