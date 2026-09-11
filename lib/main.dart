import 'dart:convert';
import 'dart:math' as math;
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:google_fonts/google_fonts.dart';

const Color usAccent = Color(0xFFD32F2F);
const Color usDark = Color(0xFF121212);
const Color usDisplay = Color(0xFF0A0A0A);
const Color usCanvas = Color(0xFFF8F9FA);
const Color usGrid = Color(0xFFE2E8F0);
const Color usText = Color(0xFF1A1A1A);
const Color usMargin = Color(0xFFE53935);

TextStyle _usFont({double? size, FontWeight? weight, double? letterSpacing, double? height, Color? color}) =>
    GoogleFonts.jetBrainsMono(fontSize: size, fontWeight: weight, letterSpacing: letterSpacing, height: height, color: color);

const MethodChannel _hostChannel = MethodChannel('idopaste/host');
const int ttlMillis = 2 * 24 * 60 * 60 * 1000;

void main() {
  WidgetsFlutterBinding.ensureInitialized();
  runApp(const IdoPasteApp());
}

class IdoPasteApp extends StatelessWidget {
  const IdoPasteApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'IDOPASTE',
      debugShowCheckedModeBanner: false,
      theme: _lightTheme,
      darkTheme: _darkTheme,
      themeMode: ThemeMode.system,
      home: const PasteScreen(),
    );
  }

  ThemeData get _lightTheme => ThemeData(
        useMaterial3: true,
        scaffoldBackgroundColor: usCanvas,
        colorScheme: const ColorScheme.light(
          primary: usAccent,
          onPrimary: Colors.white,
          surface: usCanvas,
          onSurface: usText,
          onSurfaceVariant: Color(0xFF525252),
          outline: usGrid,
          outlineVariant: usGrid,
          surfaceContainerHighest: Color(0xFFF0F0F0),
        ),
        appBarTheme: AppBarTheme(
          backgroundColor: usCanvas,
          foregroundColor: usText,
          surfaceTintColor: Colors.transparent,
          elevation: 0,
          centerTitle: false,
          titleTextStyle: _usFont(size: 18, weight: FontWeight.w700, letterSpacing: 0.08, color: usText),
          iconTheme: const IconThemeData(color: usText, size: 22),
        ),
        textTheme: TextTheme(
          bodyLarge: _usFont(size: 15, height: 1.5, color: usText),
          bodyMedium: _usFont(size: 13, height: 1.5, color: usText),
          labelLarge: _usFont(size: 11, weight: FontWeight.w500, letterSpacing: 0.08),
        ),
        elevatedButtonTheme: ElevatedButtonThemeData(
          style: ElevatedButton.styleFrom(
            backgroundColor: usAccent,
            foregroundColor: Colors.white,
            side: const BorderSide(color: usAccent),
            shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(6)),
            padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 10),
            textStyle: _usFont(size: 11, weight: FontWeight.w500, letterSpacing: 0.08),
            minimumSize: const Size(0, 38),
            elevation: 0,
          ),
        ),
        outlinedButtonTheme: OutlinedButtonThemeData(
          style: OutlinedButton.styleFrom(
            foregroundColor: usText,
            side: const BorderSide(color: usText),
            shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(6)),
            padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 10),
            textStyle: _usFont(size: 11, weight: FontWeight.w500, letterSpacing: 0.08),
            minimumSize: const Size(0, 38),
          ),
        ),
        cardTheme: CardTheme(
          color: usCanvas,
          surfaceTintColor: Colors.transparent,
          elevation: 0,
          shape: RoundedRectangleBorder(
            side: const BorderSide(color: usGrid),
            borderRadius: BorderRadius.circular(8),
          ),
        ),
        dividerTheme: const DividerThemeData(
          color: usGrid,
          thickness: 1,
        ),
      );

  ThemeData get _darkTheme => ThemeData(
        useMaterial3: true,
        scaffoldBackgroundColor: usDisplay,
        colorScheme: const ColorScheme.dark(
          primary: usAccent,
          onPrimary: Colors.white,
          surface: Color(0xFF1A1A1A),
          onSurface: Color(0xFFE2E8F0),
          onSurfaceVariant: Color(0xFFA0A0A0),
          outline: Color(0xFF2A2A2A),
          outlineVariant: Color(0xFF2A2A2A),
          surfaceContainerHighest: Color(0xFF222222),
        ),
        appBarTheme: AppBarTheme(
          backgroundColor: usDark,
          foregroundColor: const Color(0xFFE2E8F0),
          surfaceTintColor: Colors.transparent,
          elevation: 0,
          centerTitle: false,
          titleTextStyle: _usFont(size: 18, weight: FontWeight.w700, letterSpacing: 0.08, color: const Color(0xFFE2E8F0)),
          iconTheme: const IconThemeData(color: Color(0xFFE2E8F0), size: 22),
        ),
        textTheme: TextTheme(
          bodyLarge: _usFont(size: 15, height: 1.5, color: const Color(0xFFE2E8F0)),
          bodyMedium: _usFont(size: 13, height: 1.5, color: const Color(0xFFE2E8F0)),
          labelLarge: _usFont(size: 11, weight: FontWeight.w500, letterSpacing: 0.08, color: const Color(0xFFE2E8F0)),
        ),
        elevatedButtonTheme: ElevatedButtonThemeData(
          style: ElevatedButton.styleFrom(
            backgroundColor: usAccent,
            foregroundColor: Colors.white,
            side: const BorderSide(color: usAccent),
            shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(6)),
            padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 10),
            textStyle: _usFont(size: 11, weight: FontWeight.w500, letterSpacing: 0.08),
            minimumSize: const Size(0, 38),
            elevation: 0,
          ),
        ),
        outlinedButtonTheme: OutlinedButtonThemeData(
          style: OutlinedButton.styleFrom(
            foregroundColor: const Color(0xFFE2E8F0),
            side: const BorderSide(color: Color(0xFFE2E8F0)),
            shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(6)),
            padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 10),
            textStyle: _usFont(size: 11, weight: FontWeight.w500, letterSpacing: 0.08),
            minimumSize: const Size(0, 38),
          ),
        ),
        cardTheme: CardTheme(
          color: const Color(0xFF1A1A1A),
          surfaceTintColor: Colors.transparent,
          elevation: 0,
          shape: RoundedRectangleBorder(
            side: const BorderSide(color: Color(0xFF2A2A2A)),
            borderRadius: BorderRadius.circular(8),
          ),
        ),
        dividerTheme: const DividerThemeData(
          color: Color(0xFF2A2A2A),
          thickness: 1,
        ),
      );
}

class PasteClip {
  final String text;
  final int ts;
  final String type;

  const PasteClip({required this.text, required this.ts, required this.type});

  factory PasteClip.fromJson(Map<String, dynamic> json) => PasteClip(
        text: (json['text'] ?? '') as String,
        ts: (json['ts'] ?? 0) as int,
        type: (json['type'] ?? 'text') as String,
      );
}

class PasteScreen extends StatefulWidget {
  const PasteScreen({super.key});

  @override
  State<PasteScreen> createState() => _PasteScreenState();
}

class _PasteScreenState extends State<PasteScreen> with SingleTickerProviderStateMixin, WidgetsBindingObserver {
  List<PasteClip> _clips = [];
  bool _isLoading = true;
  bool _serviceEnabled = false;
  bool _recording = false;
  late AnimationController _knobController;

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addObserver(this);
    _knobController = AnimationController(vsync: this, duration: const Duration(milliseconds: 250));
    _refresh();
  }

  @override
  void dispose() {
    WidgetsBinding.instance.removeObserver(this);
    _knobController.dispose();
    super.dispose();
  }

  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    if (state == AppLifecycleState.resumed) {
      _refresh();
    }
  }

  Future<void> _refresh() async {
    try {
      final status = await _hostChannel.invokeMapMethod<String, dynamic>('status') ?? {};
      final raw = await _hostChannel.invokeMethod<List<dynamic>>('load') ?? [];
      final clips = raw.map((e) => PasteClip.fromJson(jsonDecode(jsonEncode(e)))).toList();
      if (mounted) {
        setState(() {
          _clips = clips;
          _serviceEnabled = (status['serviceEnabled'] ?? false) as bool;
          _recording = _serviceEnabled;
          _isLoading = false;
        });
      }
      if (_recording) {
        _knobController.forward();
      } else {
        _knobController.reverse();
      }
    } catch (e) {
      if (mounted) {
        setState(() => _isLoading = false);
      }
    }
  }

  Future<void> _copy(String text) async {
    try {
      await _hostChannel.invokeMethod('copy', {'text': text});
    } catch (e) {
      //
    }
    if (mounted) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text('COPIADO', style: _usFont(size: 11, weight: FontWeight.w500)),
          duration: const Duration(seconds: 1),
          behavior: SnackBarBehavior.floating,
          backgroundColor: usAccent,
          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(6)),
          margin: const EdgeInsets.all(16),
        ),
      );
      HapticFeedback.lightImpact();
    }
  }

  Future<void> _delete(int ts) async {
    try {
      await _hostChannel.invokeMethod('delete', {'ts': ts});
    } catch (e) {
      //
    }
    _refresh();
  }

  Future<void> _clearAll() async {
    try {
      await _hostChannel.invokeMethod('clear');
    } catch (e) {
      //
    }
    _knobController.reverse();
    _refresh();
  }

  Future<void> _openAccessibility() async {
    try {
      await _hostChannel.invokeMethod('openAccessibilitySettings');
    } catch (e) {
      //
    }
  }

  Future<void> _grabNow() async {
    try {
      await _hostChannel.invokeMethod('grabNow');
    } catch (e) {
      //
    }
    _refresh();
  }

  String _relativeTime(int ts) {
    final diff = DateTime.now().millisecondsSinceEpoch - ts;
    if (diff < 60 * 1000) return 'ahora';
    if (diff < 60 * 60 * 1000) return 'hace ${(diff / 60000).floor()} min';
    if (diff < ttlMillis) return 'hace ${(diff / 3600000).floor()} h';
    return 'EXPIRADO';
  }

  @override
  Widget build(BuildContext context) {
    final isDark = Theme.of(context).brightness == Brightness.dark;
    final bgColor = isDark ? usDisplay : usCanvas;
    final cardColor = isDark ? const Color(0xFF1A1A1A) : usCanvas;
    final borderColor = isDark ? const Color(0xFF2A2A2A) : usGrid;
    final textColor = isDark ? const Color(0xFFE2E8F0) : usText;
    final mutedColor = const Color(0xFF888888);

    Widget body;
    if (_isLoading) {
      body = Center(child: _UsKnob(controller: _knobController, size: 64));
    } else if (_clips.isEmpty) {
      body = _EmptyState(
        serviceEnabled: _serviceEnabled,
        onEnable: _openAccessibility,
        onGrab: _grabNow,
      );
    } else {
      body = ListView.separated(
        padding: const EdgeInsets.fromLTRB(16, 8, 16, 16),
        itemCount: _clips.length,
        separatorBuilder: (_, __) => const SizedBox(height: 8),
        itemBuilder: (context, index) {
          final clip = _clips[index];
          final isLink = clip.type == 'link';
          return Container(
            decoration: BoxDecoration(
              color: cardColor,
              border: Border.all(color: borderColor),
              borderRadius: BorderRadius.circular(8),
            ),
            padding: const EdgeInsets.all(12),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Row(
                  children: [
                    Icon(isLink ? Icons.link : Icons.content_paste, size: 14, color: isLink ? usAccent : mutedColor),
                    const SizedBox(width: 6),
                    Text(
                      isLink ? 'LINK' : 'TEXTO',
                      style: _usFont(size: 9, weight: FontWeight.w700, letterSpacing: 0.12, color: isLink ? usAccent : mutedColor),
                    ),
                    const Spacer(),
                    Text(
                      _relativeTime(clip.ts),
                      style: _usFont(size: 10, color: mutedColor),
                    ),
                  ],
                ),
                const SizedBox(height: 8),
                Text(
                  clip.text,
                  maxLines: 4,
                  overflow: TextOverflow.ellipsis,
                  style: _usFont(size: 14, height: 1.5, color: isLink ? (isDark ? const Color(0xFFE2E8F0) : const Color(0xFF144A9B)) : textColor),
                ),
                const SizedBox(height: 10),
                Row(
                  children: [
                    Expanded(
                      child: ElevatedButton.icon(
                        onPressed: () => _copy(clip.text),
                        icon: const Icon(Icons.copy, size: 14),
                        label: Text('COPIAR', style: _usFont(size: 10, weight: FontWeight.w700)),
                      ),
                    ),
                    const SizedBox(width: 8),
                    _UsIconButton(
                      icon: Icons.delete_outline,
                      tooltip: 'BORRAR',
                      onPressed: () => _delete(clip.ts),
                      isDestructive: true,
                    ),
                  ],
                ),
              ],
            ),
          );
        },
      );
    }

    return Scaffold(
      backgroundColor: bgColor,
      appBar: AppBar(
        title: Row(
          children: [
            _UsKnob(controller: _knobController, size: 30),
            const SizedBox(width: 12),
            Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  'IDOPASTE',
                  style: _usFont(size: 15, weight: FontWeight.w700, letterSpacing: 0.08, color: usText),
                ),
              ],
            ),
          ],
        ),
        actions: [
          _UsIconButton(
            icon: Icons.cloud_sync_outlined,
            tooltip: 'TOMAR PORTAPAPELES',
            onPressed: _grabNow,
          ),
          _UsIconButton(
            icon: Icons.settings_accessibility,
            tooltip: 'ACCESIBILIDAD',
            onPressed: _openAccessibility,
          ),
          _UsIconButton(
            icon: Icons.delete_sweep_outlined,
            tooltip: 'LIMPIAR TODO',
            onPressed: _clearAll,
            isDestructive: true,
          ),
          const SizedBox(width: 8),
        ],
      ),
      body: Column(
        children: [
          _DisplayBar(
            count: _clips.length,
            recording: _recording,
            isDark: isDark,
          ),
          Expanded(child: body),
          _FooterStats(count: _clips.length, isDark: isDark),
        ],
      ),
    );
  }
}

class _EmptyState extends StatelessWidget {
  final bool serviceEnabled;
  final VoidCallback onEnable;
  final VoidCallback onGrab;

  const _EmptyState({required this.serviceEnabled, required this.onEnable, required this.onGrab});

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Padding(
        padding: const EdgeInsets.all(32),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            const Icon(Icons.content_paste_off_outlined, size: 44, color: usGrid),
            const SizedBox(height: 16),
            Text(
              serviceEnabled ? 'SIN CAPTURAS AÚN' : 'GRABACIÓN DESACTIVADA',
              textAlign: TextAlign.center,
              style: _usFont(size: 13, weight: FontWeight.w700, letterSpacing: 0.08),
            ),
            const SizedBox(height: 8),
            Text(
              'Los textos y links copiados en cualquier app aparecerán aquí y se borrarán solos a las 48 h.',
              textAlign: TextAlign.center,
              style: _usFont(size: 11, height: 1.5, color: const Color(0xFF888888)),
            ),
            const SizedBox(height: 20),
            if (!serviceEnabled)
              ElevatedButton.icon(
                onPressed: onEnable,
                icon: const Icon(Icons.settings_accessibility, size: 16),
                label: Text('ACTIVAR ACCESIBILIDAD', style: _usFont(size: 11, weight: FontWeight.w700)),
              )
            else
              OutlinedButton.icon(
                onPressed: onGrab,
                icon: const Icon(Icons.cloud_sync_outlined, size: 16),
                label: Text('TOMAR PORTAPAPELES', style: _usFont(size: 11, weight: FontWeight.w700)),
              ),
          ],
        ),
      ),
    );
  }
}

class _UsKnob extends AnimatedWidget {
  final double size;

  const _UsKnob({required AnimationController controller, required this.size})
      : super(listenable: controller);

  @override
  Widget build(BuildContext context) {
    final progress = listenable as AnimationController;
    return SizedBox(
      width: size,
      height: size,
      child: CustomPaint(painter: _KnobPainter(progress: progress.value)),
    );
  }
}

class _KnobPainter extends CustomPainter {
  final double progress;

  _KnobPainter({required this.progress});

  @override
  void paint(Canvas canvas, Size size) {
    final center = Offset(size.width / 2, size.height / 2);
    final radius = size.width / 2;
    final bgPaint = Paint()..color = usDark..style = PaintingStyle.fill;
    final borderPaint = Paint()..color = usGrid..style = PaintingStyle.stroke..strokeWidth = 1;
    canvas.drawCircle(center, radius, bgPaint);
    canvas.drawCircle(center, radius, borderPaint);

    if (progress > 0) {
      final spiralPaint = Paint()
        ..color = usAccent.withOpacity(0.3 + 0.2 * progress)
        ..style = PaintingStyle.stroke
        ..strokeWidth = 1.5;
      for (int i = 0; i < 3; i++) {
        final path = Path();
        final startAngle = (i * 2 * math.pi / 3) + (progress * math.pi);
        for (double t = 0; t < 2 * math.pi; t += 0.1) {
          final r = radius * 0.3 + radius * 0.35 * (t / (2 * math.pi));
          final x = center.dx + r * math.cos(t + startAngle);
          final y = center.dy + r * math.sin(t + startAngle);
          if (t == 0) {
            path.moveTo(x, y);
          } else {
            path.lineTo(x, y);
          }
        }
        canvas.drawPath(path, spiralPaint);
      }
      final notchPaint = Paint()..color = usAccent;
      final notchAngle = -math.pi / 2 + progress * math.pi * 2;
      final notchStart = Offset(center.dx + (radius * 0.15) * math.cos(notchAngle), center.dy + (radius * 0.15) * math.sin(notchAngle));
      final notchEnd = Offset(center.dx + (radius * 0.45) * math.cos(notchAngle), center.dy + (radius * 0.45) * math.sin(notchAngle));
      canvas.drawLine(notchStart, notchEnd, notchPaint..strokeWidth = 2);
      final centerPaint = Paint()..color = usAccent;
      canvas.drawCircle(center, radius * 0.12, centerPaint);
    }
  }

  @override
  bool shouldRepaint(covariant CustomPainter oldDelegate) =>
      oldDelegate is _KnobPainter && oldDelegate.progress != progress;
}

class _DisplayBar extends StatelessWidget {
  final int count;
  final bool recording;
  final bool isDark;

  const _DisplayBar({required this.count, required this.recording, required this.isDark});

  @override
  Widget build(BuildContext context) {
    final bgColor = isDark ? usDark : usDisplay;
    final labelColor = isDark ? const Color(0xFF888888) : const Color(0xFF888888);
    final borderColor = isDark ? const Color(0xFF1A1A1A) : usText;
    return Container(
      width: double.infinity,
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 10),
      decoration: BoxDecoration(color: bgColor, border: Border(bottom: BorderSide(color: borderColor))),
      child: Row(
        children: [
          _DisplayItem(label: 'ITEMS', value: count.toString(), valueColor: usAccent, labelColor: labelColor, bgColor: bgColor, borderColor: borderColor),
          const SizedBox(width: 12),
          _DisplayItem(label: 'TTL', value: '48H', valueColor: labelColor, labelColor: labelColor, bgColor: bgColor, borderColor: borderColor),
          const Spacer(),
          Container(
            padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 3),
            decoration: BoxDecoration(color: recording ? usAccent : const Color(0xFF444444), borderRadius: BorderRadius.circular(999)),
            child: Text(
              recording ? 'REC' : 'OFF',
              style: _usFont(size: 8, weight: FontWeight.w700, letterSpacing: 0.1, color: Colors.white),
            ),
          ),
        ],
      ),
    );
  }
}

class _DisplayItem extends StatelessWidget {
  final String label;
  final String value;
  final Color valueColor;
  final Color labelColor;
  final Color bgColor;
  final Color borderColor;

  const _DisplayItem({required this.label, required this.value, required this.valueColor, required this.labelColor, required this.bgColor, required this.borderColor});

  @override
  Widget build(BuildContext context) {
    return Container(
      constraints: const BoxConstraints(minWidth: 64),
      padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
      decoration: BoxDecoration(color: bgColor, border: Border.all(color: borderColor), borderRadius: BorderRadius.circular(4)),
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          Text(label, style: _usFont(size: 8, letterSpacing: 0.14, color: labelColor)),
          const SizedBox(height: 2),
          Text(value, style: _usFont(size: 16, weight: FontWeight.w700, letterSpacing: 0.06, color: valueColor)),
        ],
      ),
    );
  }
}

class _FooterStats extends StatelessWidget {
  final int count;
  final bool isDark;

  const _FooterStats({required this.count, required this.isDark});

  @override
  Widget build(BuildContext context) {
    final textColor = isDark ? const Color(0xFF888888) : const Color(0xFF888888);
    final borderColor = isDark ? const Color(0xFF2A2A2A) : usGrid;
    return Container(
      width: double.infinity,
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 10),
      decoration: BoxDecoration(border: Border(top: BorderSide(color: borderColor))),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Text('AUTO-CLEAN 48H', style: _usFont(size: 8, letterSpacing: 0.1, color: textColor)),
          Text('IDOTIZA', style: _usFont(size: 8, weight: FontWeight.w700, letterSpacing: 0.1, color: usAccent)),
        ],
      ),
    );
  }
}

class _UsIconButton extends StatelessWidget {
  final IconData icon;
  final String tooltip;
  final VoidCallback onPressed;
  final bool isDestructive;

  const _UsIconButton({required this.icon, required this.tooltip, required this.onPressed, this.isDestructive = false});

  @override
  Widget build(BuildContext context) {
    final isDark = Theme.of(context).brightness == Brightness.dark;
    final color = isDestructive ? usAccent : (isDark ? const Color(0xFFE2E8F0) : usText);
    return Tooltip(
      message: tooltip,
      child: Material(
        color: Colors.transparent,
        child: InkWell(
          borderRadius: BorderRadius.circular(6),
          onTap: () {
            HapticFeedback.lightImpact();
            onPressed();
          },
          child: Padding(
            padding: const EdgeInsets.all(8),
            child: Icon(icon, color: color, size: 20),
          ),
        ),
      ),
    );
  }
}