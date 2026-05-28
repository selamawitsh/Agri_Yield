class WeatherReading {
  final String? id;
  final String? farmId;
  final double temperatureC;
  final double rainfallMm;
  final double? humidityPct;
  final bool isDryDay;
  final String forecastType;
  final int? forecastHorizonDays;
  final String recordedDate;

  WeatherReading({
    this.id,
    this.farmId,
    required this.temperatureC,
    required this.rainfallMm,
    this.humidityPct,
    required this.isDryDay,
    required this.forecastType,
    this.forecastHorizonDays,
    required this.recordedDate,
  });

  factory WeatherReading.fromJson(Map<String, dynamic> json) {
    return WeatherReading(
      id: json['id'],
      farmId: json['farmId'],
      temperatureC: (json['temperatureC'] ?? 0).toDouble(),
      rainfallMm: (json['rainfallMm'] ?? 0).toDouble(),
      humidityPct: json['humidityPct'] != null
          ? (json['humidityPct']).toDouble()
          : null,
      isDryDay: json['isDryDay'] ?? false,
      forecastType: json['forecastType'] ?? 'ACTUAL',
      forecastHorizonDays: json['forecastHorizonDays'],
      recordedDate: json['recordedDate'] ?? '',
    );
  }
}

class DroughtStatus {
  final String farmId;
  final int consecutiveDryDays;
  final int droughtThresholdDays;
  final bool isTriggered;
  final String? triggeredAt;
  final String lastChecked;

  DroughtStatus({
    required this.farmId,
    required this.consecutiveDryDays,
    required this.droughtThresholdDays,
    required this.isTriggered,
    this.triggeredAt,
    required this.lastChecked,
  });

  factory DroughtStatus.fromJson(Map<String, dynamic> json) {
    return DroughtStatus(
      farmId: json['farmId'] ?? '',
      consecutiveDryDays: json['consecutiveDryDays'] ?? 0,
      droughtThresholdDays: json['droughtThresholdDays'] ?? 30,
      isTriggered: json['isTriggered'] ?? false,
      triggeredAt: json['triggeredAt'],
      lastChecked: json['lastChecked'] ?? '',
    );
  }

  double get droughtProgress =>
      consecutiveDryDays / droughtThresholdDays;
}

class WeatherAlert {
  final String id;
  final String farmId;
  final String alertType;
  final String severity;
  final String messageEn;
  final String? messageAm;
  final String? messageOm;
  final double? forecastValue;
  final String? forecastDate;
  final String createdAt;

  WeatherAlert({
    required this.id,
    required this.farmId,
    required this.alertType,
    required this.severity,
    required this.messageEn,
    this.messageAm,
    this.messageOm,
    this.forecastValue,
    this.forecastDate,
    required this.createdAt,
  });

  factory WeatherAlert.fromJson(Map<String, dynamic> json) {
    return WeatherAlert(
      id: json['id'] ?? '',
      farmId: json['farmId'] ?? '',
      alertType: json['alertType'] ?? '',
      severity: json['severity'] ?? 'LOW',
      messageEn: json['messageEn'] ?? '',
      messageAm: json['messageAm'],
      messageOm: json['messageOm'],
      forecastValue: json['forecastValue'] != null
          ? (json['forecastValue']).toDouble()
          : null,
      forecastDate: json['forecastDate'],
      createdAt: json['createdAt'] ?? '',
    );
  }
}

class WeatherRisk {
  final String farmId;
  final double riskScore;
  final String riskLevel;

  WeatherRisk({
    required this.farmId,
    required this.riskScore,
    required this.riskLevel,
  });

  factory WeatherRisk.fromJson(Map<String, dynamic> json) {
    return WeatherRisk(
      farmId: json['farmId'] ?? '',
      riskScore: (json['riskScore'] ?? 0).toDouble(),
      riskLevel: json['riskLevel'] ?? 'LOW',
    );
  }
}
