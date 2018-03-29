// 脚本列表bootstrap-table
var $script_list = $('#scenario_list');
$script_list.bootstrapTable({
    url: '/scenarioInfoList',
    method: 'post',
    // contentType: "application/x-www-form-urlencoded",        // 如果不指定contentType,则BootstrapTable默认以application/json的形式发送请求
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
            scenarioName: $('#search_name').val(),
            status: $('#select_status').val()
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
            field: 'createTime',
            title: '创建时间',
            align: 'center',
            formatter: function (value, row, index) {
                return convertDateFormat(value);
            }
        }, {
            field: 'status',
            title: '状态',
            align: 'center',
            formatter: function (value, row, index) {
                return convertStatusFlag(value);
            }
        },
        {
            field: 'operate',
            title: '操作',
            align: 'center',
            formatter: function (value, row, index) {
                return [
                    '<a href="javascript:modScenario(' + row.id + ')">' +
                    '<i class="fa fa-pencil"></i>修改' +
                    '</a>',
                    '&nbsp&nbsp' +
                    '<a href="javascript:delScenario(' + row.id + ')">' +
                    '<i class="fa fa-times"></i>删除' +
                    '</a>',
                    '&nbsp&nbsp' +
                    '<a href="javascript:runScenario(' + row.id + ')">' +
                    '<i class="fa fa-cogs"></i>运行' +
                    '</a>' +
                    '&nbsp&nbsp' +
                    '<a href="javascript:chkScenario(' + row.id + ')">' +
                    '<i class="fa fa-eye"></i>结果' +
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


// 将状态得布尔值转成中文状态
function convertStatusFlag(status_value) {
    if (status_value === true) {
        return '启用';
    } else {
        return '停用';
    }
}