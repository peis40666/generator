import store from '../vuex/store';
import Vue from 'vue';
import moment from 'moment';
import axios from "axios"
import echarts from 'echarts'
import citmsUI from "citms-ui-package"
Object.defineProperty(Vue.prototype, '$moment', { value: moment });
Vue.prototype.$echarts = echarts
Vue.filter("previewImg", (url) => {
    return window.baseURL + url;
});

/**
 * 统一弹窗
 */
Vue.prototype.alertMessage = (() => {
    citmsUI.cToastMethod({
        text: '功能开发中,敬请期待...',
        type: "warning",
        placement: "top",
        closeOnClick: true
    })
})

/**
 * 判斷字符串或对象是否为空
 * @param name 需获取的参数名称
 */
export function isEmpty(params) {
    if (typeof(params) == "string") {
        if (params == "") return true;
        var regu = "^[ ]+$";
        var re = new RegExp(regu);
        return re.test(params);
    } else if (typeof(params) == "object") {
        let empty = true
        for (let key in params) {
            if (key) {
                empty = false
                break
            }
        }
        return empty
    }
    return null;
}
/**
 * 获取URL参数
 * @param name 需获取的参数名称
 */
export function getQueryString(name) {
    var reg = new RegExp('(^|&)' + name + '=([^&]*)(&|$)', 'i');
    var search = window.location.href.indexOf("?") == -1 ? window.location.search : "?" + window.location.href.split("?")[1];
    var r = decodeURI(search).substr(1).match(reg);
    if (r != null) {
        return unescape(r[2]);
    }
    return null;
}
/*树形结构数据转换部分*/
export function TreeUtil() {
    function addRange(loopArr, array) {
        for (var i = 0, j = array.length; i < j; i++) loopArr[loopArr.length] = array[i];
        return loopArr;
    }
    //递归添加父级数据
    function findParentNode(node, opts, view, existNodes, _idHash) {
        var parentId = node[opts.parentIdField];
        if (parentId && !existNodes[parentId]) {
            var parentNode = _idHash[parentId];
            if (parentNode) {
                view.push(citms.clone(parentNode));
                existNodes[parentId] = true;
                findParentNode(parentNode, opts, view, existNodes, _idHash);
            }
        }
    }
    return {
        /*
         * 功能：    将树形结构转换成普通的 id,pid结构数组形式
         * 参数：    nodes 树形数据
         *           nodesField 子节点字段名
         *           idField    id节点字段名
         *           parentIdField  父节点字段名
         * 返回值：  数组
         * 创建人：  杜冬军
         * 创建时间：2016-01-27
         */
        treeToArray: function(nodes, nodesField, idField, parentIdField, parentId) {
            if (!nodesField) nodesField = 'children';
            var array = [];

            for (var i = 0, l = nodes.length; i < l; i++) {
                var node = nodes[i];
                array[array.length] = node;

                if (parentIdField) node[parentIdField] = parentId;

                var childrenNodes = node[nodesField];
                if (childrenNodes && childrenNodes.length > 0) {
                    var id = node[idField];
                    var childrenArray = this.treeToArray(childrenNodes, nodesField, idField, parentIdField, id);
                    addRange(array, childrenArray);
                }

            }
            return array;
        },
        /*
         * 功能：    将普通的id,pid结构数组形式转换成树形结构
         * 参数：    data 数组树形数据
         *           nodesField 子节点字段名
         *           idField    id节点字段名
         *           parentIdField  父节点字段名
         * 返回值：  树形数据
         * 创建人：  杜冬军
         * 创建时间：2016-01-27
         */
        arrayToTree: function(data, nodesField, idField, parentIdField) {
            if (!nodesField) nodesField = 'children';
            idField = idField || '_id';
            parentIdField = parentIdField || '_pid';
            var nodes = [];
            var idHash = {};
            for (var i = 0, l = data.length; i < l; i++) {
                var o = data[i];
                if (!o) continue;
                var id = o[idField];
                if (id !== null && id !== undefined) {
                    idHash[id] = o;
                }
                delete o[nodesField];
            }
            for (var i = 0, l = data.length; i < l; i++) {
                var o = data[i];
                var p = idHash[o[parentIdField]];
                if (!p) {
                    nodes.push(o);
                    continue;
                }
                if (!p[nodesField]) {
                    p[nodesField] = [];
                }
                p[nodesField].push(o);
            }

            function setDeep(arr, dep) {
                arr.forEach(item => {
                    item.deep = dep
                    if (item[nodesField]) {
                        let deep = dep + 1
                        setDeep(item[nodesField], deep)
                    }
                })
            }
            setDeep(nodes, 0)
            return nodes;
        },
        search: function(option) {
            var defaults = {
                filterScope: function(node) { return true; }, //过滤方法 返回true则表示节点符合规则
                data: [],
                nodesField: "children",
                idField: "id",
                parentIdField: "pid",
                isTree: false, //是否树形数据 为否的则认为是列表型数据
                isMatchingChild: true //为true会匹配到每个节点，为false父级匹配上那么直接认为所有子集也是匹配上的
            };
            var opts = $.extend(false, defaults, option);
            if (!$.isFunction(opts.filterScope)) {
                return opts.data;
            } else {
                var nodes, view = [],
                    _idHash = [];
                if (opts.isTree) {
                    nodes = this.treeToArray(opts.data, opts.nodesField, opts.idField, opts.parentIdField, "");
                } else {
                    nodes = opts.data;
                }
                if (nodes && nodes.length > 0) {
                    var node;
                    for (var i = 0, l = nodes.length; i < l; i++) {
                        node = nodes[i];
                        _idHash[node[opts.idField]] = node;
                    }
                    var existNodes = [];
                    for (var i = 0, l = nodes.length; i < l; i++) {
                        node = nodes[i];
                        if (opts.filterScope(node) !== false) {
                            view.push(citms.clone(node));
                            existNodes[node[opts.idField]] = true;
                            findParentNode(node, opts, view, existNodes, _idHash);
                        }
                    }
                    return this.arrayToTree(view, opts.nodesField, opts.idField, opts.parentIdField);
                }
                return [];
            }


        }
    };
}
/**
 * 导出Excel
 * @param options.columns 必选，列头名称
 * @param options.data 必选，如果是导出选中参数类型为数组，如果是导出全部为字符串URL
 * @param options.title 必选，导出文件的名称
 * @param options.methods 可选，如果data为url则填写请求方式 默认为post
 * @param options.params 可选，如果data是url，则可以传过滤条件
 * @param options.extra 可选，数组格式，额外的添加项
 */
export function exportExcel(options) {
    let ColumnInfo = [];
    if (!options.columns || !options.data || !options.title) {
        return Promise.reject("参数错位,columns、data、title必选");
    }
    if (options.columns.length > 0) {
        for (let i = 0; i < options.columns.length; i++) {
            let item = options.columns[i]
            if (options.columns[i].dataIndex) {
                let jsonCol = {};
                jsonCol["Align"] = item.align || "left";
                jsonCol["Header"] = item.title;
                jsonCol["Field"] = item.dataIndex;
                ColumnInfo.push(jsonCol);
            }
        }
        if (options.extra)
            ColumnInfo = ColumnInfo.concat(options.extra)
        let exportParam = {
            "FileName": options.title,
            "ColumnInfoList": ColumnInfo,
            "Type": options.methods || "post",
            "ColAsSerialize": options.ColAsSerialize || true,
        };
        if (typeof(options.data) != "string") {
            if (options.data.length > 0) {
                exportParam["Data"] = options.data;
                exportParam["IsExportSelectData"] = true;
            } else {
                return Promise.reject("请至少选择一条数据");
            }
        } else {
            exportParam['Condition'] = options.params;
            exportParam['Api'] = options.data;
            exportParam['IsExportSelectData'] = false;
            exportParam['ColAsSerialize'] = true;
        }
        return new Promise((resolve, reject) => {
            let xhr = new window.XMLHttpRequest()
            xhr.open('post', baseURL + "/common/Excel/Export", true)
            xhr.setRequestHeader("Authorization", 'Basic ' + Token);
            xhr.setRequestHeader("Content-Type", 'application/json');
            xhr.onload = () => {
                let response = JSON.parse(xhr.response)
                if (!response.code) {
                    let fragment = document.createDocumentFragment();
                    let a = document.createElement('a');
                    a.setAttribute("href", response.result.url);
                    fragment.appendChild(a);
                    document.body.appendChild(fragment);
                    a.click();
                    a.remove();
                    resolve()
                } else {
                    reject(response.message)
                }
            }
            xhr.send(JSON.stringify(exportParam))
            xhr.onerror = (err) => {
                reject(err)
            }
        })

    } else {
        return Promise.reject("当前表格没有列");
    }
}
/**
 * 图片展示
 * @param node 当前Img元素
 * @param parentArea 如果展示元素为显示元素或为已知宽高元素，则之间传宽高对象，如果为隐藏元素则传图片的父级元素
 * @param hideNode 如果为隐藏元素则传控制该元素隐藏的元素
 */
window.setImagePosition = (node, parentArea, hideNode) => {
    if (typeof(parentArea) == 'boolean') {
        var img = new Image();
        img.src = node.getAttribute("src");
        img.onload = function() {
            if (this.width > this.height)
                node.style.width = "100%"
            else
                node.style.height = "100%"
        }
        return
    }
    if (hideNode) {
        var finalStyle = hideNode.currentStyle ? hideNode.currentStyle : document.defaultView.getComputedStyle(hideNode, null)
        if (finalStyle.display == "none") {
            hideNode.style.display = "block"
            hideNode.style.visibility = "hidden"
            let parentNode = parentArea
            parentArea = { h: parentNode.clientHeight, w: parentNode.clientWidth }
            hideNode.style.display = "none"
            hideNode.style.visibility = "visible"
        } else {
            let parentNode = parentArea
            parentArea = { h: parentNode.clientHeight, w: parentNode.clientWidth }
        }
    }
    let parentNode = node.parentNode
    let position = parentNode.style.position
    let inBlockStr = node.getAttribute("image-rect")
        //if(!position)
        //  parentNode.style.position = "relative"
    node.style.position = "absolute"
    var img = new Image();
    img.src = node.getAttribute("src");
    node.parentNode.childNodes.forEach(item => {
        if (item.className && item.className.indexOf('red-block') != -1) {
            node.parentNode.removeChild(item)
        }
    })
    img.onload = function() {
        let width = this.width,
            height = this.height,
            wplv = parentArea.w / width,
            hplv = parentArea.h / height,
            wVal = (width - parentArea.w) / width,
            hVal = (height - parentArea.h) / height;;
        if (width <= parentArea.w && height <= parentArea.h) {
            let imageTop = (parentArea.h - height) / 2,
                imageLeft = (parentArea.w - width) / 2
            node.style.width = width + "px"
            node.style.height = height + "px"
            node.style.top = imageTop + "px"
            node.style.left = imageLeft + "px"
            if (inBlockStr) {
                let inBlock = JSON.parse(inBlockStr),
                    newBlock = inBlock
                let Block = document.createElement("div")
                Block.className = "red-block"
                Block.style.position = "absolute"
                Block.style.left = newBlock[0] - 25 + imageLeft + "px"
                Block.style.top = newBlock[1] - 10 + imageTop + "px"
                Block.style.width = (newBlock[2] + 50) + "px"
                Block.style.height = (newBlock[3] + 20) + "px"
                Block.style.border = "2px solid red";
                node.parentNode.appendChild(Block)
            }
            return;
        }
        if (wVal >= hVal) {
            let imageTop = (parentArea.h - wplv * height) / 2
            node.style.width = parentArea.w + "px"
            node.style.height = (parentArea.w / width) * height + "px"
            node.style.top = imageTop + "px"
            node.style.left = 0
            if (inBlockStr) {
                let inBlock = JSON.parse(inBlockStr),
                    newBlock = []
                inBlock.forEach(item => {
                    newBlock.push(item * wplv)
                })
                let Block = document.createElement("div")
                Block.className = "red-block"
                Block.style.position = "absolute"
                Block.style.left = newBlock[0] - 25 + "px"
                Block.style.top = newBlock[1] - 10 + imageTop + "px"
                Block.style.width = (newBlock[2] + 50) + "px"
                Block.style.height = (newBlock[3] + 20) + "px"
                Block.style.border = "2px solid red";
                node.parentNode.appendChild(Block)
            }
        } else if (wVal < hVal) {
            let imageLeft = (parentArea.w - hplv * width) / 2
            node.style.width = (parentArea.h / height) * width + "px"
            node.style.height = parentArea.h + "px"
            node.style.left = imageLeft + "px"
            node.style.top = 0
            if (inBlockStr) {
                let inBlock = JSON.parse(inBlockStr),
                    newBlock = []
                inBlock.forEach(item => {
                    newBlock.push(item * hplv)
                })
                let Block = document.createElement("div")
                Block.className = "red-block"
                Block.style.position = "absolute"
                Block.style.left = newBlock[0] - 25 + imageLeft + "px"
                Block.style.top = newBlock[1] - 10 + "px"
                Block.style.width = (newBlock[2] + 50) + "px"
                Block.style.height = (newBlock[3] + 20) + "px"
                Block.style.border = "2px solid red";
                node.parentNode.appendChild(Block)
            }
        }
    }
}
window.imageError = (node) => {
        node.setAttribute("src", "../../static/images/picNoFound.png")
    }
    /**
     *   页面添加水印
     *   @param name 用户名
     *   @param department 部门
     */
window.addWaterMark = function(name, department) {
        var canvas = document.createElement("canvas");
        canvas.id = "watermark"
        document.body.className = "hasWaterMark"
        document.body.appendChild(canvas)
        var WaterMarkImg = document.getElementById("watermark"),
            WaterMarkContent = WaterMarkImg.getContext("2d"),
            ContentWidth = 1;
        if (department) {
            ContentWidth = department.length * 22;
            if (ContentWidth < 240)
                ContentWidth = 240;
        }
        WaterMarkImg.width = ContentWidth;
        WaterMarkImg.height = ContentWidth / Math.sqrt(2);
        WaterMarkContent.save();
        WaterMarkContent.translate(0, 0);
        WaterMarkContent.rotate((Math.PI * -40) / 180);
        WaterMarkContent.font = "normal normal normal 18px/24px Microsoft YaHei";
        WaterMarkContent.fillStyle = "#ddd";
        //添加姓名和时间
        WaterMarkContent.fillText(name, -WaterMarkImg.height / 2 + 35, WaterMarkImg.height / 2 + (24 * 1));
        WaterMarkContent.fillText(department, -WaterMarkImg.height / 2 + 35, WaterMarkImg.height / 2 + (24 * 2));
        WaterMarkContent.fillText(moment().format("YYYY-MM-DD HH:mm:ss"), -WaterMarkImg.height / 2 + 35, WaterMarkImg.height / 2 + (24 * 3));

        WaterMarkContent.fill();
        WaterMarkContent.restore();
        var WaterMarkBase64 = WaterMarkImg.toDataURL();
        if (WaterMarkBase64.length > 20) {
            var background = "url('" + WaterMarkBase64 + "')";
            document.body.style.background = '#fff' + " " + background
        }
    }
    /**
     *   获取权限公用方法
     *   @param currentCode 当前功能代码
     *   @param actionCode 动作代码
     */
export function getAuthority(currentCode, actionCode) {
    let isAdmin = store.getters.getUserInfo.isAdmin,
        PermissionList = store.getters.getUserInfo.permissionList,
        thisPermission = PermissionList.filter(item => item.functionCode == currentCode)[0]
    if (isAdmin) return true
    if (!thisPermission)
        return false
    return thisPermission.actionCodeList.indexOf(actionCode) != -1
}
Object.defineProperty(Vue.prototype, 'getAuthority', { value: getAuthority });
/**
 * 日期格式化
 * @param date 需要格式化的日期
 * @param format 格式化参数 YYYY-MM-DD HH:mm:ss
 */
export function formatDate(date, format) {
    format = format || 'YYYY-MM-DD HH:mm:ss';
    return moment(date).format(format);
}
Object.defineProperty(Vue.prototype, '$formatDate', { value: formatDate });