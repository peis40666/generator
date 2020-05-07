$(function () {

    $.ajax({
        type: 'get',
        url: 'sys/generator/ds',
        dataType: 'json',
        success(res) {
            let arr = [...res.dsList];
            let newArr = arr.map((item,index) => {
                if(index===0) {
                    getTabel(item.name)
                    return `<option selected value="${item.name}" class="datasourceItem">${item.name}</option>`
            }
                return `<option value="${item.name}" class="datasourceItem">${item.name}</option>`
            });

            newArr.forEach((item) => {
                $('#datasource').append(item)
            })
        }
    });

    function getTabel(name) {
        $("#jqGrid").jqGrid({
            url: 'sys/generator/list',
            datatype: "json",
            colModel: [
                { label: '表名', name: 'tableName', width: 100, key: true },
                { label: 'Engine', name: 'engine', width: 70},
                { label: '表备注', name: 'tableComment', width: 100 },
                { label: '创建时间', name: 'createTime', width: 100 }
            ],
            viewrecords: true,
            height: 385,
            rowNum: 10,
            rowList : [10,30,50,100,200],
            rownumbers: true,
            rownumWidth: 25,
            autowidth:true,
            multiselect: true,
            pager: "#jqGridPager",
            jsonReader : {
                root: "page.list",
                page: "page.currPage",
                total: "page.totalPage",
                records: "page.totalCount"
            },
            postData:{
                'datasource':name
            },
            prmNames : {
                page:"page",
                rows:"limit",
                order: "order",
            },

            gridComplete:function(){
                //隐藏grid底部滚动条
                $("#jqGrid").closest(".ui-jqgrid-bdiv").css({ "overflow-x" : "hidden" });
            }
        });
    }

});

var vm = new Vue({
	el:'#rrapp',
	data:{
		q:{
			tableName: null,
			moduleName:null,
            datasource:null
		}
	},
	methods:{
		query: function () {
		    let name = $('#datasource').val();
			$("#jqGrid").jqGrid('setGridParam',{ 
                postData:{
                    'tableName': vm.q.tableName,
                    'datasource':name
                },
                page:1,
            }).trigger("reloadGrid");
		},
		generator: function() {
            var tableNames = getSelectedRows();
            if(tableNames == null){
                return ;
            }
            var moduleName = vm.q.moduleName;
            if(moduleName == null){
            	alert("请填写服务标识");
                return ;
            }
            location.href = "sys/generator/code?tables=" + tableNames.join()+"&moduleName="+moduleName;
		},
		stopPropagation: function(e){
			e.stopPropagation();
			return false;
		}
	}
});

