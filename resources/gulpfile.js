var gulp = require('gulp');
var less = require('gulp-less');
var runSequence = require('run-sequence');

gulp.task('watch', function(){
  return gulp.watch([
    'public/**/*.less'
  ], ['less']);
});

gulp.task('less', function () {
  return gulp.src(['public/less/index.less'])
    .pipe(less())
    .pipe(gulp.dest('public/css/'));
});

gulp.task('default', function () {
  runSequence('watch', 'less', function () {});
});
