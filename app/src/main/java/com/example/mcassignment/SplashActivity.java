package com.example.mcassignment;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

public class SplashActivity {

    public class BrainView extends View {
        private Paint paint;
        private Path path;

        public BrainView(Context context) {
            super(context);
            init();
        }

        private void init() {
            paint = new Paint();
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(4f);
            paint.setAntiAlias(true);

            path = new Path();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            // Clear canvas with transparent background
            canvas.drawColor(Color.TRANSPARENT);

            int width = getWidth();
            int height = getHeight();

            // Main brain hemispheres (two large circles)
            float centerX = width / 2f;
            float centerY = height / 2f;
            float radius = Math.min(width, height) * 0.4f;

            // Left hemisphere
            canvas.drawCircle(centerX - radius * 0.3f, centerY, radius, paint);

            // Right hemisphere
            canvas.drawCircle(centerX + radius * 0.3f, centerY, radius, paint);

            // Corpus callosum (connecting lines)
            path.reset();
            path.moveTo(centerX - radius * 0.15f, centerY - radius * 0.2f);
            path.lineTo(centerX + radius * 0.15f, centerY - radius * 0.2f);
            path.moveTo(centerX - radius * 0.15f, centerY + radius * 0.2f);
            path.lineTo(centerX + radius * 0.15f, centerY + radius * 0.2f);
            canvas.drawPath(path, paint);

            // Brain folds (smaller circles)
            float smallRadius = radius * 0.15f;

            // Left hemisphere folds
            canvas.drawCircle(centerX - radius * 0.5f, centerY - radius * 0.3f, smallRadius, paint);
            canvas.drawCircle(centerX - radius * 0.4f, centerY + radius * 0.4f, smallRadius, paint);
            canvas.drawCircle(centerX - radius * 0.6f, centerY + radius * 0.1f, smallRadius, paint);

            // Right hemisphere folds
            canvas.drawCircle(centerX + radius * 0.5f, centerY - radius * 0.3f, smallRadius, paint);
            canvas.drawCircle(centerX + radius * 0.4f, centerY + radius * 0.4f, smallRadius, paint);
            canvas.drawCircle(centerX + radius * 0.6f, centerY + radius * 0.1f, smallRadius, paint);
        }
    }
}
