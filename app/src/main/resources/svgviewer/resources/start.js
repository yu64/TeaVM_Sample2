
function fetchNextPath(nowPath, callback)
{
    let endpoint = "/next" + (nowPath == null ? "" : "?path=" + nowPath);
    fetch(endpoint).then(res => res.text()).then(callback);
}

function getchPrevPath(nowPath, callback)
{
    let endpoint = "/prev" + (nowPath == null ? "" : "?path=" + nowPath);
    fetch(endpoint).then(res => res.text()).then(callback);
}

window.addEventListener("load", function(){

    callTeavmScript();
});
