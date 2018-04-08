// 脚本列表bootstrap-table
var $result_list = $('#result_list');
$result_list.bootstrapTable({
    url: '/getScenarioNewResultList',
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
            limit: params.limit
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
            field: 'scenarioName',
            title: '场景名称',
            align: 'center'
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

// 查看历史结果
function resSampler(resultId) {
    $(window).attr('location', "/scenarioSampleDetailChartGrid?resultId=" + resultId);
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