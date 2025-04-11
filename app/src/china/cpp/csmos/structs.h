//
// Created by 33688 on 2024/12/4.
//

#ifndef RNMOS_STRUCTS_H
#define RNMOS_STRUCTS_H

typedef enum
{
    NA_NULL = 0,
    NA_LOOPBACK,
    NA_BROADCAST,
    NA_IP,
} netadrtype_t;

typedef struct netadr_s {
    netadrtype_t	type;
    unsigned char	ip[4];
    unsigned short	port;
} netadr_t;

struct bf_read {
public:
    // The current buffer.
    const unsigned char* /*RESTRICT*/ m_pData;
    int						m_nDataBytes;
    int						m_nDataBits;

    // Where we are in the buffer.
    int				m_iCurBit;
private:
    // Errors?
    bool			m_bOverflow;
    // For debugging..
    bool			m_bAssertOnOverflow;
    const char		*m_pDebugName;
};

typedef struct netpacket_s
{
    netadr_t		from;		// sender IP
    int				source;		// received source
    double			received;	// received time
    unsigned char	*data;		// pointer to raw packet data
    bf_read			message;	// easy bitbuf data access
    int				size;		// size in bytes
    int				wiresize;   // size in bytes before decompression
    bool			stream;		// was send as stream
    struct netpacket_s *pNext;	// for internal use, should be NULL in public
} netpacket_t;


const int k_cbMaxGameServerGameDir = 32;
const int k_cbMaxGameServerMapName = 32;
const int k_cbMaxGameServerGameDescription = 64;
const int k_cbMaxGameServerName = 64;
const int k_cbMaxGameServerTags = 128;
const int k_cbMaxGameServerGameData = 2048;

class servernetadr_t {
public:
    uint16_t	m_usConnectionPort;	// (in HOST byte order)
    uint16_t	m_usQueryPort;
    uint32_t  m_unIP;
};

class gameserveritem_t
{
public:
    gameserveritem_t();

    const char* GetName() const;
    void SetName( const char *pName );

public:
    servernetadr_t m_NetAdr;									///< IP/Query Port/Connection Port for this server
    int m_nPing;												///< current ping time in milliseconds
    bool m_bHadSuccessfulResponse;								///< server has responded successfully in the past
    bool m_bDoNotRefresh;										///< server is marked as not responding and should no longer be refreshed
    char m_szGameDir[k_cbMaxGameServerGameDir];					///< current game directory
    char m_szMap[k_cbMaxGameServerMapName];						///< current map
    char m_szGameDescription[k_cbMaxGameServerGameDescription];	///< game description
    uint32_t m_nAppID;											///< Steam App ID of this server
    int m_nPlayers;												///< total number of players currently on the server.  INCLUDES BOTS!!
    int m_nMaxPlayers;											///< Maximum players that can join this server
    int m_nBotPlayers;											///< Number of bots (i.e simulated players) on this server
    bool m_bPassword;											///< true if this server needs a password to join
    bool m_bSecure;												///< Is this server protected by VAC
    uint32_t m_ulTimeLastPlayed;									///< time (in unix time) when this server was last played on (for favorite/history servers)
    int	m_nServerVersion;										///< server version as reported to Steam

public:

    /// Game server name
    char m_szServerName[k_cbMaxGameServerName];

    // For data added after SteamMatchMaking001 add it here
public:
    /// the tags this server exposes
    char m_szGameTags[k_cbMaxGameServerTags];

    /// steamID of the game server - invalid if it's doesn't have one (old server, or not connected to Steam)
//    CSteamID m_steamID; // 咱也不知道这个大小
};

#define MAX_GAME_DESCRIPTION 8192
#define MAX_SERVER_NAME 2048
#define MAX_PATH 260

class newgameserver_t
{
public:
    newgameserver_t() = default;

    netadr_t m_NetAdr;								///< IP/Query Port/Connection Port for this server
    int m_nPing;											///< current ping time in milliseconds
    int m_nProtocolVersion;
    bool m_bHadSuccessfulResponse;	///< server has responded successfully in the past
    bool m_bDoNotRefresh;						///< server is marked as not responding and should no longer be refreshed
    char m_szGameDir[MAX_PATH];				 ///< current game directory
    char m_szMap[MAX_PATH];					///< current map
    char m_szGameTags[MAX_PATH];
    char m_szGameDescription[MAX_GAME_DESCRIPTION]; ///< game description

    int m_nPlayers;
    int m_nMaxPlayers;										///< Maximum players that can join this server
    int m_nBotPlayers;										///< Number of bots (i.e simulated players) on this server
    bool m_bPassword;										///< true if this server needs a password to join

    int m_iFlags;

    /// Game server name
    char m_szServerName[MAX_SERVER_NAME];
};

#endif //RNMOS_STRUCTS_H
