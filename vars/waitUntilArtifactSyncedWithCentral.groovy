def call(body) {
    // evaluate the body block, and collect configuration into the object
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    def flow = new io.fabric8.Fabric8Commands()

    stage "waiting for ${config.artifact} ${config.version} artifacts to sync with central"
    node ('swarm'){

      waitUntil {
        flow.getMavenCentralVersion(config.artifact) == config.version
      }

      message =  "${config.artifact} ${config.version} released and available in maven central"
      hubot room: 'release', message: message

    }
}
