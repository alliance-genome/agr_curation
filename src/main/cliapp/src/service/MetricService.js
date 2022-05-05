import { merge } from "immutable";
import { BaseAuthService } from './BaseAuthService';

export class MetricService extends BaseAuthService {

    getMetrics() {
      return this.api.get('/metrics.json').then(res => this.parseMetrics(res.data));
    }

    parseMetrics(results) {
      let mets = {};

      //let newColumns = [];
      
      for(let key in results) {
        let names = key.split(";");
        let subKeys = names[0].split(".");
        //console.log(subKeys);
        let subKey = mets;
        for(let idx in subKeys) {
          if(!subKey[subKeys[idx]]) {
            subKey[subKeys[idx]] = {};
            //subKey[subKeys[idx]]["label"] = subKeys[idx];
            //subKey[subKeys[idx]]["children"] = [];
          }
          subKey = subKey[subKeys[idx]];
        }

        subKey["key"] = names[0];
        //subKey["data"] = {};
        //subKey["label"] = names[0];
        //subKey = subKey["data"];

        subKey["values"] = [];
        for(let nameidx in names) {
          if(nameidx > 0) {
            let keyv = names[nameidx].split("=");
            let map = {};
            map[keyv[0]] = keyv[1];
            subKey["values"].push(map);
            //if(!newColumns.includes(keyv[0])) {
            //  newColumns.push(keyv[0]);
            //}
          }
        }

        if(typeof results[key] === 'object') {
          //console.log(results[key]);
          for(let vkey in results[key]) {
            let map = {};
            map[vkey] = results[key][vkey];
            subKey["values"].push(map);
          }
        } else {
          subKey["values"].push({ metricValue: results[key]});
        }

        
      }
      //console.log(newColumns);
      return this.parse_mets(mets, "", "").children;
    };

    parse_mets(mets, key, label) {

      if(mets.key) {
        let ret = {};
        //ret["label"] = label;
        ret["key"] = mets.key;
        ret["data"] = { name: label };
        if(mets.values) {
          for(let vkey in mets.values) {
            //(mets.values[vkey]).forEach((k, v) => ret["data"].set(k, v));
            ret["data"] = merge(ret["data"], mets.values[vkey]);
            //console.log(mets.values[vkey]);
          }
        }
        return ret;
      } else {
        let ret = {};
        if(key || label) {
          //ret["label"] = label;
          ret["key"] = key;
          ret["data"] = { name: label };
        }
        let children = [];
        for(let k in mets) {
          children.push(this.parse_mets(mets[k], key + "." + k, k));
        }
        ret["children"] = children;
        return ret;
      }


    };

}
