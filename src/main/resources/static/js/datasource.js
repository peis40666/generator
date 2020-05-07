$(function () {

    $.ajax({
        type: 'get',
        url: 'sys/generator/ds',
        dataType: 'json',
        success(res) {
            console.log(res);
            var arr = res.dsList;
            for(var i=0;i<arr.length;i++){
                var tr = '<tr role="row" id="1" tabindex="-1" class="jqgrow ui-row-ltr">';
                var trTail = '</tr>';
                var rowNum = '<td role="gridcell" class="jqgrid-rownum active"\n' +
                    '                    style="text-align: center; width: 25px;" title="1"\n' +
                    '                    aria-describedby="jqGrid_rn">'+(i+1)+'</td>';
                var name = '<td role="gridcell" style="" title=""\n' +
                    '                    aria-describedby="jqGrid_modulName">'+arr[i].name+'</td>';
                var driverName = '<td role="gridcell" style="" title=""\n' +
                    '                    aria-describedby="jqGrid_version">'+arr[i].driverClassName+'</td>';
                var url = '<td role="gridcell" style="" title=""\n' +
                    '                    aria-describedby="jqGrid_version">'+arr[i].url+'</td>';
                var username = '<td role="gridcell" style="" title=""\n' +
                    '                    aria-describedby="jqGrid_version">'+arr[i].username+'</td>';
                var password = '<td role="gridcell" style="" title=""\n' +
                    '                    aria-describedby="jqGrid_version">'+arr[i].password+'</td>'
                $('#dsTbody').append(tr+rowNum+name+driverName+url+username+password+trTail);
            }

        }
    });
})