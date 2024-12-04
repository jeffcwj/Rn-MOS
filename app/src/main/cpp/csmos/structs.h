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

#endif //RNMOS_STRUCTS_H
