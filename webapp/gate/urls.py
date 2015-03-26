from django.conf.urls import patterns, url

urlpatterns = patterns(
    'gate.views',
    url(r'^$', 'index'),
    url(r'^api/(?P<url>.+)/$', 'api'),
)
