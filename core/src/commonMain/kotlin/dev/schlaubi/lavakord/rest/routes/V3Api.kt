package dev.schlaubi.lavakord.rest.routes

import dev.schlaubi.lavakord.UnsafeRestApi
import dev.schlaubi.lavakord.rest.routes.V3Api.Sessions.Specific
import io.ktor.resources.*

@Resource("version")
@UnsafeRestApi
internal class Version

@Resource("/v3")
@UnsafeRestApi
internal class V3Api {

    @Resource("websocket")
    data class WebSocket(val api: V3Api = V3Api())

    @Resource("sessions")
    data class Sessions(val api: V3Api = V3Api()) {

        @Resource("{sessionId}")
        data class Specific(val sessionId: String, val sessions: Sessions = Sessions()) {
            @Resource("players")
            data class Players(val specific: Sessions.Specific) {
                constructor(sessionId: String) : this(Specific(sessionId))

                @Resource("{guildId}")
                data class Specific(val guildId: ULong, val noReplace: Boolean? = null, val players: Players) {
                    constructor(guildId: ULong, sessionId: String, noReplace: Boolean? = false) : this(
                        guildId,
                        noReplace,
                        Players(sessionId)
                    )
                }
            }
        }
    }

    @Resource("loadtracks")
    data class LoadTracks(val identifier: String, val api: V3Api = V3Api())

    @Resource("decodetrack")
    data class DecodeTrack(val encodedTrack: String? = null, val api: V3Api = V3Api())

    @Resource("info")
    data class Info(val api: V3Api = V3Api())

    @Resource("stats")
    data class Stats(val stats: V3Api = V3Api())

    @Resource("routeplanner")
    data class RoutePlanner(val api: V3Api = V3Api()) {
        @Resource("status")
        data class Status(val routePlanner: RoutePlanner = RoutePlanner())

        @Resource("free")
        data class Free(val routePlanner: RoutePlanner = RoutePlanner()) {

            @Resource("address")
            data class Address(val free: Free = Free())

            @Resource("all")
            data class All(val free: Free = Free())
        }
    }
}