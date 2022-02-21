import React, { useState, useEffect } from 'react';
import { TreeTable } from 'primereact/treetable';
import { Column } from 'primereact/column';
import { Button } from 'primereact/button';
import { MetricService } from '../service/MetricService';

export const MetricsComponent = () => {

  const [metrics, setMetrics] = useState(null);
  const [refresh, setRefresh] = useState(false);
  
  const metricService = new MetricService();

  useEffect(() => {
    metricService.getMetrics().then(results => {
      console.log(results);
      setMetrics(results)
    });
  }, [refresh]);

  return (
      <div className="grid nested">
        <div className="col-12">
          <div class="card">
            <div class="flex justify-content-between">
              <h2>System Stats</h2>
              <Button onClick={() => setRefresh(!refresh)} label="Refresh Table" />
            </div>
            <TreeTable value={metrics}>
              <Column field="name" header="Name" expander></Column>
              <Column field="metricValue" header="Value"></Column>
              <Column field="id" header="id"></Column>
              <Column field="area" header="area"></Column>
              <Column field="runtime" header="runtime"></Column>
              <Column field="vendor" header="vendor"></Column>
              <Column field="version" header="version"></Column>
              <Column field="state" header="state"></Column>
              
              <Column field="pool" header="pool"></Column>
            </TreeTable>
          </div>
        </div>
      </div>
  )
}
