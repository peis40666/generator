
export default {
    mock(api){
        this.mockApi(api);
    },
    mockApi(api){
        //替换接口请求地址

        //构建要mock的api列表
        let mockApiList = [];
        if(mockConfig){
            if(mockConfig.simpleMock&&mockConfig.simpleMock.length>0){
                for(let index in mockConfig.simpleMock){
                    let Model = mockConfig.simpleMock[index];
                    mockApiList.push({
                        apiName:"search"+Model,
                        type:"search",
                        apiDataFileUrl:'/static/mock/'+Model+'Mock.js'
                    });
                    mockApiList.push({
                        apiName:"view"+Model,
                        type:"view",
                        apiDataFileUrl:'/static/mock/'+Model+'Mock.js'
                    });
                    mockApiList.push({
                        apiName:"save"+Model,
                        type:"save"
                    });
                    mockApiList.push({
                        apiName:"delete"+Model,
                        type:"delete"
                    });
                    mockApiList.push({
                        apiName:"update"+Model,
                        type:"update"
                    });
                }
            }
            if(mockConfig.greatMock&&mockConfig.greatMock.length>0){
                for(let index in mockConfig.greatMock){
                    mockApiList.push(mockConfig.greatMock[index]);
                }
            }
        }

        //通过mock api列表mock列表中的api
        if(mockApiList.length>0){
            for(let index in mockApiList){
                let mApi = mockApiList[index];
                 //搜索
                 api[mApi.apiName] = function(params) {
                    if(mApi.type == "search"){
                        return new Promise((resolve, reject) => { 
                            let mockDataUrl = mApi.apiDataFileUrl+'?'+new Date().getTime();   
                            getMockData(mockDataUrl).then(data => {
                                
                                let res = {
                                    code:0,
                                }
                                filterSearchData(res,data,params);
                                resolve(res);
                            });  
                            
                        });
                    }else if(mApi.type == "view"){
                        return new Promise((resolve, reject) => { 
                            let mockDataUrl = mApi.apiDataFileUrl+'?'+new Date().getTime();                     
                            getMockData(mockDataUrl).then(data => {                           
                                let res = {
                                    code:0,
                                }
                                filterObjData(res,data,params);
                                resolve(res);
                            });  
                            
                        });
                    }else if(mApi.type == "save"){
                        return successRes();
                    }else if(mApi.type == "update"){
                        return successRes();
                    }else if(mApi.type == "delete"){
                        return successRes();
                    }     
                    
                };
            }
        }
    }

}
function successRes(){
    return new Promise((resolve, reject) => { 
        let res = {
            code:0,
            result:{},
            message:"操作成功"
        }
        resolve(res);   
    });  
}

function filterSearchData(res,data,params){
    let rdata = [];
    if(data){
        rdata = data.filter((d)=>{
            let flag = true;
            for(let p in params){
                if(d[p]&&d[p]!=params[p]){
                    flag = false;
                    break;
                }
              } 
            return flag;
        }); 
    }
    res.result = rdata;
    res.totalCount = rdata.length;
}

function filterObjData(res,data,params){
    let rdata = [];
    if(data){
        rdata = data.filter((d)=>{
            let flag = true;
            for(let p in params){
                if(d[p]&&d[p]!=params[p]){
                    flag = false;
                    break;
                }
              } 
            return flag;
        }); 
    }
    res.result = rdata?rdata[0]:{};
    res.totalCount = 1;
}

function getMockData(path){

    return new Promise((resolve, reject) => { 
        var httpRequest = new XMLHttpRequest();//第一步：创建需要的对象
        httpRequest.open('get', path, true); //第二步：打开连接/***发送json格式文件必须设置请求头 ；如下 - */
        httpRequest.setRequestHeader("Content-type","application/json");//设置请求头 注：post方式必须设置请求头（在建立连接后设置请求头）var obj = { name: 'zhansgan', age: 18 };
        httpRequest.send(JSON.stringify({}));//发送请求 将json写入send中
        /**
         * 获取数据后的处理程序
         */
        httpRequest.onreadystatechange = function () {//请求后的回调接口，可将请求成功后要执行的程序写在其中
            if (httpRequest.readyState == 4 && httpRequest.status == 200) {//验证请求是否发送成功
                var json = httpRequest.responseText;//获取到服务端返回的数据
                resolve(JSON.parse(json));
            }
        };
    });

   
}