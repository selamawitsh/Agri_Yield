import 'package:flutter_test/flutter_test.dart';
import 'package:merchant_pos_app/main.dart';
import 'package:merchant_pos_app/services/auth_service.dart';

void main() {
  testWidgets('App loads', (WidgetTester tester) async {
    await tester.pumpWidget(
      MyApp(
        authService: AuthService(),
      ),
    );

    expect(find.byType(MyApp), findsOneWidget);
  });
}