 kubectl -n default exec testclient -- /usr/bin/kafka-topics --zookeeper kafka-1594993188-zookeeper:2181 --list
 
 kubectl exec -i -t testclient -- bash
 
 kubectl -n default exec testclient -- /usr/bin/kafka-topics --zookeeper kafka-1594993188-zookeeper:2181 --list
 
 kubectl -n default exec testclient -- /usr/bin/kafka-topics --zookeeper kafka-1594993188-zookeeper:2181 --topic test1 --create --partitions 1
 
 kubectl -n default exec -ti testclient -- /usr/bin/kafka-console-consumer --bootstrap-server kafka-1594993188:9092 --topic tweetz-Trump --from-beginning
 
 
 
 kubectl -n default exec testclient -- /usr/bin/kafka-topics --zookeeper mikes-kafka-zookeeper:2181 --topic Trump-tweetz --create --partitions 1 --replication-factor 1
 
 kubectl -n default exec testclient -- /usr/bin/kafka-topics --zookeeper mikes-kafka-zookeeper:2181 --list
 
 kubectl -n default exec testclient -- /usr/bin/kafka-console-consumer --bootstrap-server mikes-kafka-0:9092 --topic Trump-tweetz --from-beginning