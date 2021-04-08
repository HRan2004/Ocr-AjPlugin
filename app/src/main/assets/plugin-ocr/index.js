
module.exports = function(plugin){
    const runtime = plugin.runtime;
    const scope = plugin.topLevelScope;

    function ocr(){

    }

    ocr.detect = function(bitmap,ratio){
        ratio = ratio || 1
        return plugin.detect(bitmap,ratio)
    }

    ocr.filterScore = function(results,dbnetScore,angleScore,crnnScore){
        return plugin.filterScore(results,dbnetScore,angleScore,crnnScore)
    }

    return ocr;
}