Highcharts.chart('container', {
    chart: {
        zoomType: 'x'
    },
    boost: {
        useGPUTranslations: true
    },
    title: {
        text: 'Highcharts drawing ' + n + ' points'
    },
    subtitle: {
        text: 'Using the Boost module'
    },
    tooltip: {
        valueDecimals: 2
    },
    series: [
        {
            data: data,
            lineWidth: 0.5
        }
    ]
});