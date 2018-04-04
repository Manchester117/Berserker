var stompClient = null;
var samplerInfo = null;
var samplerChartFilter = {};
var samplerTableFilter = {};
var rowId = 0;

/**
 * @description         WebSocket建立连接,获取实时数据
 */
function sampleResultSocket(chart, dataType) {
    var sock = new SockJS('/socket');
    stompClient = Stomp.over(sock);
    stompClient.connect({}, function (frame) {
        // console.log('connected ' + frame);
        stompClient.subscribe('/sampleResult/data', function (data) {
            // 拿到数据后将字符串转成JSON对象
            samplerInfo = $.parseJSON(data.body);
            paintingChart(chart, dataType);
            paintingTable();
            samplerInfo = null;
        });
    });
}

/**
 * @description         添加数据系列,绘制数据点
 * @param chart
 * @param dataType
 */
function paintingChart(chart, dataType) {
    if (samplerInfo) {
        var timeStamp = samplerInfo["timeStamp"];
        var samplerLabel = samplerInfo["samplerLabel"];
        var samplerData = samplerInfo[dataType];

        if (samplerChartFilter[samplerLabel] === undefined) {
            var samplerDataList = [];
            samplerDataList.push({
                x: timeStamp,
                y: samplerData
            });
            // 注意!不能在data中使用生成数组的方法
            samplerChartFilter[samplerLabel] = chart.addSeries({
                name: samplerLabel,
                data: samplerDataList
            });
        } else {
            samplerChartFilter[samplerLabel].addPoint([timeStamp, samplerData], true, false, true);   // 第三个参数,设置为false,说明将以前的数据积累
        }
    }
}

function paintingTable() {
    if (samplerInfo) {
        var samplerLabel = samplerInfo["samplerLabel"];
        if (samplerTableFilter[samplerLabel] === undefined) {
            var trRowId = 'samplerResult_' + rowId;
            var addSamplerResultRow = '<tr id="' + trRowId + '">'+
                                            '<td>' + samplerInfo['samplerLabel'] + '</td>' +
                                            '<td>' + samplerInfo['meanTime'] + '</td>' +
                                            '<td>' + samplerInfo['minTime'] + '</td>' +
                                            '<td>' + samplerInfo['maxTime'] + '</td>' +
                                            '<td>' + samplerInfo['standardDeviation'] + '</td>' +
                                            '<td>' + samplerInfo['errorPercentage'] + '</td>' +
                                            '<td>' + samplerInfo['requestRate'] + '</td>' +
                                            '<td>' + samplerInfo['receiveKBPerSecond'] + '</td>' +
                                            '<td>' + samplerInfo['sentKBPerSecond'] + '</td>' +
                                            '<td>' + samplerInfo['avgPageBytes'] + '</td>' +
                                      '</tr>';
            $(".table").append(addSamplerResultRow);
            samplerTableFilter[samplerLabel] = trRowId;
            rowId++;        // 在添加了一个系列之后,rowId自加,为后面的系列做准备.
        } else {
            var samplerResultRow = $("#" + samplerTableFilter[samplerLabel]);
            var newSamplerResultRow = '<td>' + samplerInfo['samplerLabel'] + '</td>' +
                                      '<td>' + samplerInfo['meanTime'] + '</td>' +
                                      '<td>' + samplerInfo['minTime'] + '</td>' +
                                      '<td>' + samplerInfo['maxTime'] + '</td>' +
                                      '<td>' + samplerInfo['standardDeviation'] + '</td>' +
                                      '<td>' + samplerInfo['errorPercentage'] + '</td>' +
                                      '<td>' + samplerInfo['requestRate'] + '</td>' +
                                      '<td>' + samplerInfo['receiveKBPerSecond'] + '</td>' +
                                      '<td>' + samplerInfo['sentKBPerSecond'] + '</td>' +
                                      '<td>' + samplerInfo['avgPageBytes'] + '</td>';

            // 删除原有数据
            samplerResultRow.empty();
            // 更新数据
            samplerResultRow.append(newSamplerResultRow);
        }
    }
}

/**
 * @description HighCharts的全局时区设置
 */
Highcharts.setOptions({
    global: {
        useUTC: false
    }
});

/**
 * @description HighCharts配置项
 */
var chartId = $("input[name='chartId']").val();
$(chartId).highcharts({
    series: [],
    chart: {
        renderTo: chartId,
        type: 'spline',
        animation: false,           // 去掉动画
        marginRight: 10,
        events: {
            load: function () {
                sampleResultSocket(this, $("input[name='dataType']").val());
            }
        }
    },
    boost: {                        // 启用boost.js,使用WebGL进行渲染.
        useGPUTranslations: true
    },
    title: {
        text: $("input[name='chartTitle']").val() + '-运行状态'
    },
    xAxis: {
        type: 'datetime',
        tickPixelInterval: 50,
        rotation: 50
    },
    yAxis: {
        title: {
            text: $("input[name='unit']").val(),
            style: {
                fontWeight: 'bold'
            }
        },
        plotLines: [{
            value: 0,
            width: 1,
            color: '#808080'
        }]
    },
    plotOptions: {
        series: {
            marker: {
                enabled: true             // 显示数据点
            },
            lineWidth: 2                  // 线宽
        }
    },
    tooltip: {
        formatter: function () {
            return '<b>' + this.series.name + '</b><br/>' +
                Highcharts.dateFormat('%Y-%m-%d %H:%M:%S', this.x) + '<br/>' +
                Highcharts.numberFormat(this.y, 2);
        }
    },
    legend: {
        enabled: true
    },
    exporting: {
        enabled: false
    },
    credits: {
        enabled: false                  // 不显示LOGO
    }
});

// 在页面关闭时,断开WebSocket连接
$(window).unload(function(){
    if (stompClient !== null)
        stompClient.disconnect();
});

