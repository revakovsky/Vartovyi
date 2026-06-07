package com.revakovskyi.vartovyi.constants

import com.revakovskyi.vartovyi.model.PopularChannelRegion
import com.revakovskyi.vartovyi.model.PopularTelegramChannel

/**
 * Curated suggestions for the Telegram-channel input. Never persisted and never modified at
 * runtime — already-added channels are simply hidden from the list. Display names must stay
 * pre-cleaned (trimmed, single spaces) so the stored value equals [PopularTelegramChannel.displayName].
 */
val POPULAR_TELEGRAM_CHANNELS: List<PopularTelegramChannel> = listOf(
    // National
    PopularTelegramChannel(
        handle = "@kpszsu",
        displayName = "Повітряні Сили ЗС України",
        region = PopularChannelRegion.NATIONAL,
    ),
    PopularTelegramChannel(
        handle = "@vanek_nikolaev",
        displayName = "Николаевский Ванёк",
        region = PopularChannelRegion.NATIONAL,
    ),
    PopularTelegramChannel(
        handle = "@monitor_ukr",
        displayName = "Моніторинг | Україна",
        region = PopularChannelRegion.NATIONAL,
    ),
    PopularTelegramChannel(
        handle = "@eRadarrua",
        displayName = "🔊⚠️єРадар | Повітряна тривога | Ракетна небезпека",
        region = PopularChannelRegion.NATIONAL,
    ),
    PopularTelegramChannel(
        handle = "@war_monitor",
        displayName = "Моніторинг військової активності",
        region = PopularChannelRegion.NATIONAL,
    ),
    PopularTelegramChannel(
        handle = "@war_raketaua",
        displayName = "🚀 РАКЕТА UA | Тривога України",
        region = PopularChannelRegion.NATIONAL,
    ),

    // Kyiv
    PopularTelegramChannel(
        handle = "@tryvoga_kyiv_radar",
        displayName = "Тривожні ночі Київ/ Київщина/ Повітряна тривога",
        region = PopularChannelRegion.KYIV,
    ),
    PopularTelegramChannel(
        handle = "@kievreal1",
        displayName = "Реальний Київ | Украина",
        region = PopularChannelRegion.KYIV,
    ),
    PopularTelegramChannel(
        handle = "@kyivoperat",
        displayName = "✙ Київ Оперативний | Kyiv Operative ✙",
        region = PopularChannelRegion.KYIV,
    ),

    // Zhytomyr
    PopularTelegramChannel(
        handle = "@zt_radar",
        displayName = "MNR | монітор Житомир",
        region = PopularChannelRegion.ZHYTOMYR,
    ),
    PopularTelegramChannel(
        handle = "@radarzhytomir",
        displayName = "Житомир Монітор",
        region = PopularChannelRegion.ZHYTOMYR,
    ),
    PopularTelegramChannel(
        handle = "@radar_d",
        displayName = "📢РАДАР🔊⚠️ (ЖИТОМИР) ОБЛАСТЬ)",
        region = PopularChannelRegion.ZHYTOMYR,
    ),
    PopularTelegramChannel(
        handle = "@zhytomyralarm",
        displayName = "Повітряна тривога Житомир (Житомирська область)",
        region = PopularChannelRegion.ZHYTOMYR,
    ),
    PopularTelegramChannel(
        handle = "@zhytomyr_alert",
        displayName = "ЖИТОМИР / СИРЕНИ / ПОВІТРЯНА ТРИВОГА",
        region = PopularChannelRegion.ZHYTOMYR,
    ),
    PopularTelegramChannel(
        handle = "@zhitomirq",
        displayName = "Житомир Повітряна Тривога",
        region = PopularChannelRegion.ZHYTOMYR,
    ),

    // Chernihiv
    PopularTelegramChannel(
        handle = "@chernigiv_radar",
        displayName = "Чернігів Радар",
        region = PopularChannelRegion.CHERNIHIV,
    ),
    PopularTelegramChannel(
        handle = "@ChernigivOperative",
        displayName = "Чернігів Оперативний",
        region = PopularChannelRegion.CHERNIHIV,
    ),
    PopularTelegramChannel(
        handle = "@trevoga_chernigov",
        displayName = "ПОВІТРЯНА ТРИВОГА ЧЕРНІГІВ",
        region = PopularChannelRegion.CHERNIHIV,
    ),

    // Sumy
    PopularTelegramChannel(
        handle = "@sumy_radar",
        displayName = "🇺🇦Сумщина | Радар🚀",
        region = PopularChannelRegion.SUMY,
    ),
    PopularTelegramChannel(
        handle = "@povitryana_tryvoga",
        displayName = "Суми Повітряна Тривога",
        region = PopularChannelRegion.SUMY,
    ),
    PopularTelegramChannel(
        handle = "@sumygo",
        displayName = "СУМИ / SUMY GO ∆",
        region = PopularChannelRegion.SUMY,
    ),

    // Kharkiv
    PopularTelegramChannel(
        handle = "@monitor1654",
        displayName = "monitor 1654 | Харків",
        region = PopularChannelRegion.KHARKIV,
    ),
    PopularTelegramChannel(
        handle = "@tlknewsua",
        displayName = "TLK News",
        region = PopularChannelRegion.KHARKIV,
    ),

    // Poltava
    PopularTelegramChannel(
        handle = "@PoltavaRadar",
        displayName = "Полтава радар | Radar Poltava",
        region = PopularChannelRegion.POLTAVA,
    ),
    PopularTelegramChannel(
        handle = "@region_poltava_syrena",
        displayName = "ПОЛТАВА НОВИНИ | СИРЕНА",
        region = PopularChannelRegion.POLTAVA,
    ),
    PopularTelegramChannel(
        handle = "@poltava_pvp",
        displayName = "PVP.POLTAVA",
        region = PopularChannelRegion.POLTAVA,
    ),

    // Dnipro
    PopularTelegramChannel(
        handle = "@dnipro_alertsx",
        displayName = "Дніпро Радар | Тривога",
        region = PopularChannelRegion.DNIPRO,
    ),
    PopularTelegramChannel(
        handle = "@live_dnepr",
        displayName = "ДНЕПР LIVE 24/7 🇺🇦 ДНІПРО РАДАР | ALERTS",
        region = PopularChannelRegion.DNIPRO,
    ),

    // Zaporizhzhia
    PopularTelegramChannel(
        handle = "@zp_radar",
        displayName = "Радар⚡️Запоріжжя | Запорожье",
        region = PopularChannelRegion.ZAPORIZHZHIA,
    ),
    PopularTelegramChannel(
        handle = "@gnilayachereha",
        displayName = "∆✙🍒Гнила черешня🇺🇦✙∆",
        region = PopularChannelRegion.ZAPORIZHZHIA,
    ),

    // Mykolaiv
    PopularTelegramChannel(
        handle = "@mykolaivskaODA",
        displayName = "Віталій Кім / Миколаївська ОДА",
        region = PopularChannelRegion.MYKOLAIV,
    ),

    // Kherson
    PopularTelegramChannel(
        handle = "@radarkherson",
        displayName = "Радар Херсон",
        region = PopularChannelRegion.KHERSON,
    ),
    PopularTelegramChannel(
        handle = "@kherson_monitoring",
        displayName = "Херсонщина Моніторинг🇺🇦",
        region = PopularChannelRegion.KHERSON,
    ),

    // Odesa
    PopularTelegramChannel(
        handle = "@odessa_inform",
        displayName = "ОДЕССА ИНФО LIVE - радар Одеса",
        region = PopularChannelRegion.ODESA,
    ),
    PopularTelegramChannel(
        handle = "@xydessa",
        displayName = "Хуевая Одесса",
        region = PopularChannelRegion.ODESA,
    ),
)
