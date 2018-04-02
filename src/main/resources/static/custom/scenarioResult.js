// 脚本列表bootstrap-table
var $result_list = $('#result_list');
$result_list.bootstrapTable({
    url: '/getScenarioResultList',
    method: 'post',
    dataType: 'json',
    striped: true,
    pagination: true,
    sidePagination: 'server',
    pageSize: 15,
    pageList: [15, 30, 60],
    paginationPreText: '‹',
    paginationNextText: '›',
    locale: 'zh-CN',
    queryParams: function (params) {
        return {
            offset: params.offset,
            limit: params.limit,
            scenarioId: $('#scenario_id').val()
        }
    },
    columns: [
        {
            field: 'id',
            title: '编号',
            align: 'center',
            formatter: function(value, row, index) {            // 以列表的序列来标号
                return index + 1;
            }
        }, {
            field: 'numThreads',
            title: '并发数量',
            align: 'center'
        }, {
            field: 'rampUp',
            title: '攀升时间',
            align: 'center'
        }, {
            field: 'duration',
            title: '持续时间',
            align: 'center'
        },{
            field: 'runTime',
            title: '运行时间',
            align: 'center',
            formatter: function (value, row, index) {
                return convertDateFormat(value);
            }
        },
        {
            field: 'operate',
            title: '操作',
            align: 'center',
            formatter: function (value, row, index) {
                return [
                    '<a href="javascript:resSampler(' + row.id + ')">' +
                    '<i class="fa fa-eye"></i>查看' +
                    '</a>' +
                    '&nbsp&nbsp' +
                    '<a href="javascript:delResult(' + row.id + ')">' +
                    '<i class="fa fa-times"></i>删除' +
                    '</a>'
                ].join('');
            }
        }
    ]
});

// 转换日期格式(时间戳转换为datetime格式)
function convertDateFormat(date_value) {
    var date_val = date_value + "";
    if (date_value != null) {
        var date = new Date(parseInt(date_val.replace("/Date(", "").replace(")/", ""), 10));
        var month = date.getMonth() + 1 < 10 ? "0" + (date.getMonth() + 1) : date.getMonth() + 1;
        var currentDate = date.getDate() < 10 ? "0" + date.getDate() : date.getDate();

        var hours = date.getHours() < 10 ? "0" + date.getHours() : date.getHours();
        var minutes = date.getMinutes() < 10 ? "0" + date.getMinutes() : date.getMinutes();
        var seconds = date.getSeconds() < 10 ? "0" + date.getSeconds() : date.getSeconds();

        return date.getFullYear() + "-" + month + "-" + currentDate + " " + hours + ":" + minutes + ":" + seconds;
    }
}

// 查看历史结果
function resSampler(resultId) {

}

// 删除历史结果
function delResult(resultId) {
    $.ajax({
        url: '/delScenarioResult',
        type: 'post',
        data: {
            resultId: resultId
        },
        success: function(result) {
            bootbox.alert({
                title: '提示',
                message: result['message'],
                callback: function () {
                    $result_list.bootstrapTable('selectPage', 1);
                }
            });
        }
    });
}