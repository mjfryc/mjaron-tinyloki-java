# Docker test server

Sample test server configuration used to try the log sender.

Reference: <https://grafana.com/docs/loki/latest/setup/install/docker/>

Start the server with:

```bash
# cd tinyloki-server
docker compose up
```
Following services are available:

* Check if Loki server is ready: <http://localhost:3100/ready>
* Open Grafana dashboard: <http://localhost:3000>
