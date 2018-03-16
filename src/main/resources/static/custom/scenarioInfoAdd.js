var $script_file = $("#script_file");
// 脚本同步上传
$script_file.fileinput({
    allowedFileExtensions: ["jmx"],
    maxFileCount: 1,
    language : "zh",
    showUpload: false,
    showRemove: true,
    overwriteInitial: true,                           // 覆盖同名文件
    uploadAsync: false,
    msgPlaceholder: "Select jmx文件...",
    required: true
});

// 场景的表单提交
var $create_scenario_button = $("#create_scenario_button");
$create_scenario_button.click(
    function() {
        var $create_scenario_form = $("#create_scenario_form");
        $create_scenario_form.ajaxSubmit({
            url: "/addScenarioInfo",
            type: "post",
            success: function(result){
                var insertScenarioResult = result["insertScenarioResult"];
                if (result) {
                    bootbox.alert({
                        title: '提示',
                        message: insertScenarioResult.message + '<br/>' + insertScenarioResult.message,
                        callback: function () {
                            setScenarioIdAtInput(result["scenarioId"]);
                            createParamsUploadList(result["csvDataSetSlotList"]);
                            createTestPlanTree(result["scenarioId"]);
                        }
                    });
                } else {
                    bootbox.alert({
                        title: '警告',
                        message: "不要重复提交场景"
                    });
                }
            },
            error: function() {
                bootbox.alert({
                    title: '警告',
                    message: "请求错误"
                });
            }
        });
    }
);

// 生成参数文件的上传表单
function createParamsUploadList(result) {
    if (result){
        var $param_file_upload_list = $("#param_file_upload_list");
        $param_file_upload_list.empty();
        for (var index = 0; index < result.length; index++) {
            $param_file_upload_list.append("<div class='form-group'>\n" +
                                           "    <label for='csv_data_set_slot_"+index+"' class='col-sm-2 control-label'>"+result[index]["testElementName"]+"</label>\n" +
                                           "    <div class='col-sm-9'>\n" +
                                           "        <input name='csvDataSetName' type='hidden' value='"+result[index]["testElementName"]+"'/>\n" +
                                           "        <input id='csv_data_set_slot_"+index+"' name='paramFiles' type='file' class='file' data-show-preview='false' data-allowed-file-extensions='[\"csv\"]' data-msg-placeholder='"+result[index]["fileName"]+"'/>\n" +
                                           "    </div>\n" +
                                           "</div>");
        }
        var $modify_script_file = $(".file");
        $modify_script_file.fileinput({
            maxFileCount: 1,
            language : "zh",
            showUpload: false,
            showRemove: true,
            overwriteInitial: true,                             // 覆盖同名文件
            uploadAsync: false
        });
    }
}

// 生成测试用例的预览树
function createTestPlanTree(scenarioId) {
    var $script_tree = $("#create_script_tree");
    $.ajax({
        url: "/getScenarioScriptDataStructure",
        type: "post",
        data : {
            scenarioId: scenarioId
        },
        success: function(data) {
            $script_tree.treeview({
                data: data["scriptDataStructure"]
            });
            $script_tree.treeview('expandAll', {levels: 10, silent: true});
        },
        error: function(result) {}
    });
}

function setScenarioIdAtInput(scenarioId) {
    var $scenario_id_input = $("#scenario_id_input");
    $scenario_id_input.attr("value", scenarioId);
}

// 参数文件上传提交
var $create_csv_data_set_button = $("#create_csv_data_set_button");
$create_csv_data_set_button.click(
    function() {
        var $create_param_file_slot_list = $("#create_param_file_slot_list");
        $create_param_file_slot_list.ajaxSubmit({
            url: "/saveScenarioParamFiles",
            type: "post",
            success: function (result) {
                var status = result.status;
                var message = result.message;
                if (status === "Success") {
                    var insertParamFileMsg = "";
                    var insertParamFileMsgList = result["insertParamFileResultList"];
                    if (insertParamFileMsgList) {
                        // 如果脚本中存在CSV Data Set
                        for (var index = 0; index < insertParamFileMsgList.length; index++) {
                            insertParamFileMsg = insertParamFileMsg + insertParamFileMsgList[index].message + ": " + insertParamFileMsgList[index].object + "<br/>";
                        }
                        message = "场景创建完毕" + "<br/>" + message + "<br/>" + insertParamFileMsg;
                    } else {
                        // 如果脚本中不存在CSV Data Set
                        message = "场景创建完毕"
                    }
                    bootbox.alert({
                        title: "提示",
                        message: message,
                        callback: function () {
                            window.location.href = "/";             // 回到场景列表页
                        }
                    });
                } else if (status === "Fail") {
                    bootbox.alert({
                        title: "警告",
                        message: result.message
                    });
                }
            }
        });
    }
);

// 重置场景表单
var $clear_scenario_button = $("#clear_scenario_button");
$clear_scenario_button.click(
    function() {
        $('#create_scenario_form').resetForm();
    }
);

// 重置参数表单
var $clear_csv_slot_button = $("#clear_csv_slot_button");
$clear_csv_slot_button.click(
    function() {
        $('#create_param_file_slot_list').resetForm();
    }
);



