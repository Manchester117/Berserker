var chartData = null;

function getSampleResultDetailData(chart) {
    $.ajax({
        url: "/getSampleResultDetailData",
        type: "post",
        data: {
            resultId: $("#result_id").val(),
            dataType: $("#data_type").val()
        },
        success: function (resultData) {
            chartData = resultData;
            // 遍历对象,找出每个Series
            Object.keys(chartData).forEach(function (key) {
                console.log(key, chartData[key]);
                chart.addSeries({
                    name: key,
                    data: chartData[key]
                });
            });
        }
    });
}

Highcharts.setOptions({
    global: {
        useUTC: false
    }
});


$("#container").highcharts({
    series: [],
    chart: {
        type: 'spline',
        animation: false,           // 去掉动画
        marginRight: 10,
        events: {
            load: function () {
                getSampleResultDetailData(this);
            }
        }
    },
    boost: {
        useGPUTranslations: true
    },
    title: {
        text: $("#chart_title").val()
    },
    xAxis: {
        type: 'datetime',
        tickPixelInterval: 50,
        rotation: 50
    },
    yAxis: {
        title: {
            text: $("#unit").val(),
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
                enabled: false             // 显示数据点
            },
            lineWidth: 2                   // 线宽
        }
    },
    legend: {
        enabled: true
    },
    credits: {
        enabled: false                     // 不显示LOGO
    }
});
