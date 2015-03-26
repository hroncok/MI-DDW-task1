from django.core.urlresolvers import reverse
from django.http import HttpResponseRedirect
from django.shortcuts import render
from django.utils.safestring import mark_safe
import forms
import logic


def index(request):
    '''The homepage'''
    plots = {}
    if request.method == 'POST':
        form = forms.URLForm(request.POST)
        if form.is_valid():
            url = form.cleaned_data['url']
            major, minor = logic.tokenize(url)
            plots['major'] = mark_safe(logic.flot_data(major))
    else:
        form = forms.URLForm()
    return render(request, 'index.html', {'form': form, 'plots': plots})
